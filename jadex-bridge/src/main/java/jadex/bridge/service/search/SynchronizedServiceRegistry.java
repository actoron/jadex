package jadex.bridge.service.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import jadex.base.PlatformConfiguration;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.registry.IRegistryListener;
import jadex.commons.IAsyncFilter;
import jadex.commons.IFilter;
import jadex.commons.IResultCommand;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;

/**
 *  Synchronization for service registries.
 *  
 *  The syncglobal allows allows to state whether calls to the
 *  global search methods should hold the read lock or not.
 *  
 *  In case of the old search based registry this is not allowed
 *  because the registry them might block writes changes up to the
 *  deftimeout of the platform.
 *  
 *  In case of the new synchronization based registry the lock
 *  should be held.
 */
public class SynchronizedServiceRegistry implements IServiceRegistry
{
	/** The reader count. */
	protected int readercnt; 
//	protected Map<Integer, String> readers = new HashMap<Integer, String>();
	
	/** Flag that a writer is performing updates. */
	protected ReentrantLock lock = new ReentrantLock();
	
	/** The scheduled write actions (deferred when readers are currrently reading). */
	protected List<Tuple2<IResultCommand<IFuture<Void>, Void>, Future<Void>>> writeactions = new ArrayList<Tuple2<IResultCommand<IFuture<Void>,Void>, Future<Void>>>();
	
	/** The service registry calls are delegated to. */
	protected IServiceRegistry delegate;
	
	/** Flag if global searches should be synchronized. */
	protected boolean syncglobal;
	
	//-------- constructors --------
	
	/**
	 *  Create a new SynchronizedMultiServiceRegistry.
	 */
	public SynchronizedServiceRegistry(boolean syncglobal, IServiceRegistry delegate)
	{
		this.delegate = delegate;
		this.syncglobal = syncglobal;
	}
	
	//-------- methods --------
	
	/**
	 *  Method that tries to get the lock and blocks otherwise (will terminate blocking after 3 seconds forcefully).
	 */
	protected void lock()
	{
		try
		{
			lock.tryLock(3000, TimeUnit.MILLISECONDS);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
			System.out.println("interrupted");
		}
	}
	
	/**
	 *  Method that unlocks the lock.
	 */
	protected void unlock()
	{
		lock.unlock();
	}

	/**
	 * 
	 * @param command
	 * @return
	 */
	protected IFuture<Void> writeAction(IResultCommand<IFuture<Void>, Void> command)
	{
		final Future<Void> ret = new Future<Void>();

		try
		{
			lock();
			
			if(readercnt==0)
			{
				command.execute(null).addResultListener(new DelegationResultListener<Void>(ret));
			}
			else
			{
				// schedule
				writeactions.add(new Tuple2<IResultCommand<IFuture<Void>, Void>, Future<Void>>(command, ret));
//				System.out.println("scheduled write due to lock used: "+command);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			// Unlock write lock after synchronous end of method (all modifications are done synchronously)
			// Will require a read lock for checks notifications (async part)
			unlock();
		}
		
		return ret;
	}
	
	/**
	 * 
	 * @param command
	 * @return
	 */
	protected IFuture<Object> readActionAsync(final IResultCommand<IFuture<Object>, Void> command, final String op)
	{
		IFuture<Object> ret = null;
		boolean done = false;
		try
		{
			readerInc(op);
			
			ret = command.execute(null);
			done = ret.isDone();
			if(!done)
			{
				if(ret instanceof ISubscriptionIntermediateFuture)
				{
					((ISubscriptionIntermediateFuture)ret).addQuietListener(new IntermediateReaderDecListener());
				}
				else
				{
					ret.addResultListener(new ReadDecListener<Object>());
				}
				
				// for debugging if all readers will evetually leave
//				Timer t = new Timer();
//				final IFuture<Object> fret = ret;
//				t.schedule(new TimerTask()
//				{
//					public void run()
//					{
//						if(!fret.isDone())
//							System.out.println("not done: "+op);
//					}
//				}, 5000);
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(ret==null || done)
				readerDec();
		}
		
		return ret;
	}
	
	/**
	 * 
	 * @param command
	 * @return
	 */
	protected Object readActionSync(IResultCommand<Object, Void> command, String op)
	{
		Object ret = null;
		try
		{
			readerInc(op);
			
			ret = command.execute(null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			readerDec();
		}
		
		return ret;
	}
	
	/**
	 *  Add a service to the registry.
	 *  @param sid The service id.
	 */
	public IFuture<Void> addService(final ClassInfo key, final IService service)
	{
		return writeAction(new IResultCommand<IFuture<Void>, Void>()
		{
			public IFuture<Void> execute(Void args)
			{
				return delegate.addService(key, service);
			}
			
			@Override
			public String toString()
			{
				return "addServiceStep: "+service.getServiceIdentifier().getServiceType().getTypeName()+" "+readercnt;
			}
		});
	}
	
	/**
	 *  Remove a service from the registry.
	 *  @param sid The service id.
	 */
	public void removeService(final ClassInfo key, final IService service)
	{
		writeAction(new IResultCommand<IFuture<Void>, Void>()
		{
			public IFuture<Void> execute(Void args)
			{
				delegate.removeService(key, service);
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(final ServiceQuery<T> query)
	{
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		
		writeAction(new IResultCommand<IFuture<Void>, Void>()
		{
			public IFuture<Void> execute(Void args)
			{
				ISubscriptionIntermediateFuture<T> fut = delegate.addQuery(query);
				fut.addIntermediateResultListener(new IntermediateDelegationResultListener<T>(ret));
				
				final Future<Void> myret = new Future<Void>();
				fut.addIntermediateResultListener(new IIntermediateResultListener<T>()
				{
					public void finished()
					{
						myret.setResult(null);
					}
					public void exceptionOccurred(Exception exception)
					{
						myret.setException(exception);
					}
					public void intermediateResultAvailable(T result)
					{
					}
					public void resultAvailable(Collection<T> result)
					{
						finished();
					}
				});
				
				return myret;
			}
		});
		
		return ret;
	}
	
	/**
	 *  Remove a service query from the registry.
	 *  @param query ServiceQuery.
	 */
	public <T> void removeQuery(final ServiceQuery<T> query)
	{
		writeAction(new IResultCommand<IFuture<Void>, Void>()
		{
			public IFuture<Void> execute(Void args)
			{
				delegate.removeQuery(query);
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Remove all service queries of a specific component from the registry.
	 *  @param owner The query owner.
	 */
	public void removeQueries(final IComponentIdentifier owner)
	{
		writeAction(new IResultCommand<IFuture<Void>, Void>()
		{
			public IFuture<Void> execute(Void args)
			{
				delegate.removeQueries(owner);
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Search for services.
	 */
	public <T> T searchService(final Class<T> type, final IComponentIdentifier cid, final String scope)
	{
		return (T)readActionSync(new IResultCommand<Object, Void>()
		{
			public Object execute(Void args)
			{
				return delegate.searchService(type, cid, scope);
			}
		}, "searchService");
	}
	
	/**
	 *  Search for services.
	 */
	public <T> Collection<T> searchServices(final Class<T> type, final IComponentIdentifier cid, final String scope)
	{
		return (Collection<T>)readActionSync(new IResultCommand<Object, Void>()
		{
			public Object execute(Void args)
			{
				return delegate.searchServices(type, cid, scope);
			}
		}, "searchServices");
	}
	
	/**
	 *  Search for service.
	 */
	public <T> T searchService(final Class<T> type, final IComponentIdentifier cid, final String scope, final IFilter<T> filter)
	{
		return (T)readActionSync(new IResultCommand<Object, Void>()
		{
			public Object execute(Void args)
			{
				return delegate.searchService(type, cid, scope, filter);
			}
		}, "searchService2");
	}
	
	/**
	 *  Search for service.
	 */
	public <T> Collection<T> searchServices(final Class<T> type, final IComponentIdentifier cid, final String scope, final IFilter<T> filter)
	{
		return (Collection<T>)readActionSync(new IResultCommand<Object, Void>()
		{
			public Object execute(Void args)
			{
				return delegate.searchServices(type, cid, scope, filter);
			}
		}, "searchServices2");
	}
	
	/**
	 *  Search for service.
	 */
	public <T> IFuture<T> searchService(final Class<T> type, final IComponentIdentifier cid, final String scope, final IAsyncFilter<T> filter)
	{
		return (IFuture<T>)readActionAsync(new IResultCommand<IFuture<Object>, Void>()
		{
			public IFuture<Object> execute(Void args)
			{
				return (IFuture<Object>)delegate.searchService(type, cid, scope, filter);
			}
		}, "searchService2");
	}
	
	/**
	 *  Search for services.
	 */
	public <T> ISubscriptionIntermediateFuture<T> searchServices(final Class<T> type, final IComponentIdentifier cid, final String scope, final IAsyncFilter<T> filter)
	{
		try
		{
		return (ISubscriptionIntermediateFuture)readActionAsync(new IResultCommand<IFuture<Object>, Void>()
		{
			public IFuture<Object> execute(Void args)
			{
				return (IFuture)delegate.searchServices(type, cid, scope, filter);
			}
		}, "searchServices3");
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Search for services.
	 */
	public <T> IFuture<T> searchGlobalService(final Class<T> type, final IComponentIdentifier cid, final IAsyncFilter<T> filter)
	{
		if(syncglobal)
		{
			return (IFuture<T>)readActionAsync(new IResultCommand<IFuture<Object>, Void>()
			{
				public IFuture<Object> execute(Void args)
				{
					return (IFuture<Object>)delegate.searchGlobalService(type, cid, filter);
				}
			}, "searchGlobalServices");
		}
		else
		{
			return delegate.searchGlobalService(type, cid, filter);
		}
	}
	
	/**
	 *  Search for services.
	 */
	public <T> ISubscriptionIntermediateFuture<T> searchGlobalServices(final Class<T> type, final IComponentIdentifier cid, final IAsyncFilter<T> filter)
	{
		if(syncglobal)
		{
			return (ISubscriptionIntermediateFuture)readActionAsync(new IResultCommand<IFuture<Object>, Void>()
			{
				public IFuture<Object> execute(Void args)
				{
					return (IFuture)delegate.searchGlobalServices(type, cid, filter);
				}
			}, "searchGlobalServices");
		}
		else
		{
			return delegate.searchGlobalServices(type, cid, filter);
		}
	}
	
	/**
	 *  Get the registry from a component.
	 */
	public static SynchronizedServiceRegistry getRegistry(IComponentIdentifier platform)
	{
		return (SynchronizedServiceRegistry)PlatformConfiguration.getPlatformValue(platform, PlatformConfiguration.DATA_SERVICEREGISTRY);
	}
	
	/**
	 *  Get the registry from a component.
	 */
	public static SynchronizedServiceRegistry getRegistry(IInternalAccess ia)
	{
		return getRegistry(ia.getComponentIdentifier());
	}
	
	/**
	 *  Get the lock and increment the readers variable.
	 */
	protected void readerInc(String op)
	{
//		System.out.println("readerInc: "+lock.isLocked()+" "+readercnt);
		
		// let readers wait until no write operation is performed
		lock();
//		readers.put(readercnt, op+": "+System.currentTimeMillis());
		readercnt++;
		unlock();
	}
	
	/**
	 *  Get the lock and decrement the readers variable.
	 */
	protected void readerDec()
	{
		// let readers wait until no write operation is performed
		lock();
//		readers.remove(readercnt);
		readercnt--;
		
		if(readercnt==0)
			performWrites();
//		else if(readercnt>1)
//			System.out.println("readers: "+readercnt+" "+readers);
		
		unlock();
	}
	
	/**
	 *  Perform the scheduled write actions.
	 */
	protected void performWrites()
	{
		// Write actions need to perform the writes synchroneously
		// If the methods are async only notifications/checks are allowed async
	
//		if(writeactions.size()>0)
//			System.out.println("performed writes: "+writeactions.size());
		while(writeactions.size()>0)
		{
			Tuple2<IResultCommand<IFuture<Void>, Void>, Future<Void>> tup = writeactions.remove(0);
			tup.getFirstEntity().execute(null).addResultListener(new DelegationResultListener<Void>(tup.getSecondEntity()));
		}
	}
	
	/**
	 *  Listener to decrement the reader cnt.
	 */
	protected class ReadDecListener<T> implements IResultListener<T>
	{
		public void resultAvailable(T result)
		{
			readerDec();
		}
		
		public void exceptionOccurred(Exception exception)
		{
			readerDec();
		}
	}
	
	/**
	 *  Listener to decrement the reader cnt.
	 */
	protected class IntermediateReaderDecListener<T> implements IIntermediateResultListener<T>
	{
		public void intermediateResultAvailable(T result)
		{
//			System.out.println("got ires: "+result);
		}
		
		public void finished()
		{
//			System.out.println("fini");
			readerDec();
		}
		
		public void exceptionOccurred(Exception exception)
		{
			readerDec();
		}
		
		public void resultAvailable(Collection<T> result)
		{
			readerDec();
		}
	}
	
	/**
	 *  Get the services per type.
	 *  Internally used -> always safe
	 *  @param type The type.
	 *  @return Iterator with services.
	 */
	public Iterator<IService> getServices(ClassInfo type)
	{
		return delegate.getServices(type);
	}

	/**
	 *  Add an excluded component. 
	 *  @param The component identifier.
	 */
	public void addExcludedComponent(final IComponentIdentifier cid)
	{
		writeAction(new IResultCommand<IFuture<Void>, Void>()
		{
			public IFuture<Void> execute(Void args)
			{
				delegate.addExcludedComponent(cid);
				return IFuture.DONE;
			}
		});
	}

	/**
	 *  Remove an excluded component. 
	 *  @param The component identifier.
	 */
	public IFuture<Void> removeExcludedComponent(final IComponentIdentifier cid)
	{
		return writeAction(new IResultCommand<IFuture<Void>, Void>()
		{
			public IFuture<Void> execute(Void args)
			{
				IFuture<Void> ret = delegate.removeExcludedComponent(cid);
				return ret;
			}
		});
	}

	/**
	 *  Test if a service is included.
	 *  @param ser The service.
	 *  @return True if is included.
	 */
	public boolean isIncluded(final IComponentIdentifier cid, final IService ser)
	{
		return (Boolean)readActionSync(new IResultCommand<Object, Void>()
		{
			public Object execute(Void args)
			{
				boolean ret = delegate.isIncluded(cid, ser);
				return ret;
			}
		}, "isIncluded");
	}
	
	/**
	 *  todo: WARNING: dangerous method that exposes the internal data structure
	 *  Get the service map.
	 *  @return The full service map.
	 */
	public Map<ClassInfo, Set<IService>> getServiceMap()
	{
		return (Map<ClassInfo, Set<IService>>)readActionSync(new IResultCommand<Object, Void>()
		{
			public Object execute(Void args)
			{
				Map<ClassInfo, Set<IService>> ret = delegate.getServiceMap();
				return ret;
			}
		}, "getServiceMap");
	}

	/**
	 *  Get queries per type.
	 *  @param type The interface type. If type is null all services are returned.
	 *  @return The queries.
	 */
	public <T> Set<ServiceQueryInfo<T>> getQueries(final ClassInfo type)
	{
		return (Set<ServiceQueryInfo<T>>)readActionSync(new IResultCommand<Object, Void>()
		{
			public Object execute(Void args)
			{
				Set<ServiceQueryInfo<T>> ret = delegate.getQueries(type);
				return ret;
			}
		}, "getQueries");
	}

	/**
	 *  Add an event listener.
	 *  @param listener The listener.
	 */
	public void addEventListener(final IRegistryListener listener)
	{
		writeAction(new IResultCommand<IFuture<Void>, Void>()
		{
			public IFuture<Void> execute(Void args)
			{
				delegate.addEventListener(listener);
				return IFuture.DONE;
			}
		});
	}

	/**
	 *  Remove an event listener.
	 *  @param listener The listener.
	 */
	public void removeEventListener(final IRegistryListener listener)
	{
		writeAction(new IResultCommand<IFuture<Void>, Void>()
		{
			public IFuture<Void> execute(Void args)
			{
				delegate.removeEventListener(listener);
				return IFuture.DONE;
			}
		});
	}

	/**
	 *  Get a subregistry.
	 *  @param cid The platform id.
	 *  @return The registry.
	 */
	public IServiceRegistry getSubregistry(final IComponentIdentifier cid)
	{
		return (IServiceRegistry)readActionSync(new IResultCommand<Object, Void>()
		{
			public Object execute(Void args)
			{
				IServiceRegistry ret = delegate.getSubregistry(cid);
				return ret;
			}
		}, "getSubregistry");
	}
}
