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
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;

/**
 *  Synchronized version of the multi service registry.
 */
public class SynchronizedServiceRegistry extends AbstractServiceRegistry
{
	/** The reader count. */
	protected int readercnt; 
	
	/** Flag that a writer is performing updates. */
	protected ReentrantLock lock = new ReentrantLock();
	
	/** The scheduled write actions (deferred when readers are currrently reading). */
	protected List<IResultCommand<IFuture<Void>, Void>> writeactions = new ArrayList<IResultCommand<IFuture<Void>,Void>>();
	
	/** The service registry calls are delegated to. */
	protected AbstractServiceRegistry delegate;
	
	//-------- constructors --------
	
	/**
	 *  Create a new SynchronizedMultiServiceRegistry.
	 */
	public SynchronizedServiceRegistry(AbstractServiceRegistry delegate)
	{
		this.delegate = delegate;
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
	 *  Add a service to the registry.
	 *  @param sid The service id.
	 */
	public IFuture<Void> addService(final ClassInfo key, final IService service)
	{
		final Future<Void> ret = new Future<Void>();

		lock();
		
		if(readercnt==0)
		{
			delegate.addService(key, service).addResultListener(new DelegationResultListener<Void>(ret));
		}
		else
		{
			// schedule
			writeactions.add(new IResultCommand<IFuture<Void>, Void>()
			{
				public IFuture<Void> execute(Void args)
				{
					IFuture<Void> fut = delegate.addService(key, service);
					fut.addResultListener(new DelegationResultListener<Void>(ret));
					return fut;
				}
			});
		}
		
		// Unlock write lock after synchronous end of method (all modifications are done synchronously)
		// Will require a read lock for checks notifications (async part)
		unlock();
		
		return ret;
	}
	
	/**
	 *  Remove a service from the registry.
	 *  @param sid The service id.
	 */
	public void removeService(final ClassInfo key, final IService service)
	{
		lock();

		if(readercnt==0)
		{
			delegate.removeService(key, service);
		}
		else
		{
			// schedule
			writeactions.add(new IResultCommand<IFuture<Void>, Void>()
			{
				public IFuture<Void> execute(Void args)
				{
					delegate.removeService(key, service);
					return IFuture.DONE;
				}
			});
		}
			
		// Unlock write lock after synchronous end of method (all modifications are done synchronously)
		// Will require a read lock for checks notifications (async part)
		unlock();
	}
	
	/**
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(final ServiceQuery<T> query)
	{
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		
		lock();

		if(readercnt==0)
		{
			delegate.addQuery(query).addIntermediateResultListener(new IntermediateDelegationResultListener<T>(ret));
		}
		else
		{
			// schedule
			writeactions.add(new IResultCommand<IFuture<Void>, Void>()
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
		}
		
		unlock();
		
		return ret;
	}
	
	/**
	 *  Remove a service query from the registry.
	 *  @param query ServiceQuery.
	 */
	public <T> void removeQuery(final ServiceQuery<T> query)
	{
		lock();
			
		if(readercnt==0)
		{
			delegate.removeQuery(query);
		}
		else
		{
			writeactions.add(new IResultCommand<IFuture<Void>, Void>()
			{
				public IFuture<Void> execute(Void args)
				{
					delegate.removeQuery(query);
					return IFuture.DONE;
				}
			});
		}
		
		// Unlock write lock after synchronous end of method (all modifications are done synchronously)
		// Will require a read lock for checks notifications (async part)
		unlock();
	}
	
	/**
	 *  Check the persistent queries for a new service.
	 *  @param ser The service.
	 */
	protected IFuture<Void> checkQueries(IService ser)
	{
		readerInc();
			
//		lock("checkQuery");
		IFuture<Void> ret = super.checkQueries(ser);
		ret.addResultListener(new ReadDecListener<Void>());
		
		readerDec();
		
		return ret;
	}
	
//	/**
//	 *  Check the persistent queries against a new service.
//	 *  @param it The queries.
//	 *  @param service the service.
//	 */
//	protected IFuture<Void> checkQueriesLoop(final Iterator<ServiceQueryInfo<?>> it, final IService service)
//	{
//		return super.checkQueriesLoop(it, service);
//	}
	
	/**
	 *  Check a persistent query with one service.
	 *  @param queryinfo The query.
	 *  @param service The service.
	 *  @return True, if services matches to query.
	 */
	protected IFuture<Boolean> checkQuery(final ServiceQueryInfo<?> queryinfo, final IService service)
	{
		return super.checkQuery(queryinfo, service);
	}
	
	/**
	 *  Remove all service queries of a specific component from the registry.
	 *  @param owner The query owner.
	 */
	public void removeQueries(final IComponentIdentifier owner)
	{
		lock();
		
		if(readercnt==0)
		{
			delegate.removeQueries(owner);
		}
		else
		{
			// schedule
			writeactions.add(new IResultCommand<IFuture<Void>, Void>()
			{
				public IFuture<Void> execute(Void args)
				{
					delegate.removeQueries(owner);
					return IFuture.DONE;
				}
			});
		}
			
		// Unlock write lock after synchronous end of method (all modifications are done synchronously)
		// Will require a read lock for checks notifications (async part)
		unlock();
	}
	
	/**
	 *  Search for services.
	 */
	public <T> T searchService(Class<T> type, IComponentIdentifier cid, String scope)
	{
		readerInc();
		
		T ret = super.searchService(type, cid, scope);
	
		readerDec();
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	public <T> Collection<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope)
	{
		readerInc();
		
		Collection<T> ret = super.searchServices(type, cid, scope);
	
		readerDec();
		
		return ret;
	}
	
	/**
	 *  Search for service.
	 */
	public <T> T searchService(Class<T> type, IComponentIdentifier cid, String scope, IFilter<T> filter)
	{
		readerInc();
		
		T ret = super.searchService(type, cid, scope, filter);
	
		readerDec();
		
		return ret;
	}
	
	/**
	 *  Search for service.
	 */
	public <T> Collection<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope, IFilter<T> filter)
	{
		readerInc();
		
		Collection<T> ret = super.searchServices(type, cid, scope, filter);
	
		readerDec();
		
		return ret;
	}
	
	/**
	 *  Search for service.
	 */
	public <T> IFuture<T> searchService(Class<T> type, IComponentIdentifier cid, String scope, IAsyncFilter<T> filter)
	{
		readerInc();
		
		IFuture<T> ret = super.searchService(type, cid, scope, filter);
		ret.addResultListener(new ReadDecListener<T>());
		
		return ret;
	}
	
//	/**
//	 *  Perform the search in a loop.
//	 *  @param filter The filter.
//	 *  @param it The iterator.
//	 *  @param cid The component id.
//	 *  @param scope The scope.
//	 */
//	protected <T> IFuture<T> searchLoopService(final IAsyncFilter<T> filter, final Iterator<T> it, final IComponentIdentifier cid, final String scope)
//	{
//		return super.searchLoopService(filter, it, cid, scope);
//	}
	
	/**
	 *  Search for services.
	 */
	public <T> ISubscriptionIntermediateFuture<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope, IAsyncFilter<T> filter)
	{
		readerInc();
		
		ISubscriptionIntermediateFuture<T> ret = super.searchServices(type, cid, scope, filter);
		ret.addQuietListener(new IntermediateReaderDecListener<T>());
			
		return ret;
	}
	
//	/**
//	 *  Perform the search in a loop.
//	 */
//	protected <T> ISubscriptionIntermediateFuture<T> searchLoopServices(final IAsyncFilter<T> filter, final Iterator<T> it, final IComponentIdentifier cid, final String scope)
//	{
//		return super.searchLoopServices(filter, it, cid, scope);
//	}
	
	/**
	 *  Search for services.
	 */
	public <T> IFuture<T> searchGlobalService(final Class<T> type, IComponentIdentifier cid, final IAsyncFilter<T> filter)
	{
		readerInc();
		
		IFuture<T> ret = delegate.searchGlobalService(type, cid, filter);
		ret.addResultListener(new ReadDecListener<T>());
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	public <T> ISubscriptionIntermediateFuture<T> searchGlobalServices(Class<T> type, IComponentIdentifier cid, IAsyncFilter<T> filter)
	{
		readerInc();
		
		ISubscriptionIntermediateFuture<T> ret = delegate.searchGlobalServices(type, cid, filter);
		ret.addQuietListener(new IntermediateReaderDecListener<T>());
		
		return ret;
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
	 * 
	 */
	protected void readerInc()
	{
//		System.out.println("readerInc: "+lock.isLocked()+" "+readercnt);
		
		// let readers wait until no write operation is performed
		lock();
		readercnt++;
		unlock();
	}
	
	/**
	 * 
	 */
	protected void readerDec()
	{
		// let readers wait until no write operation is performed
		lock();
		readercnt--;
		
		if(readercnt==0)
			performWrites();
		
		unlock();
	}
	
	/**
	 * 
	 */
	protected void performWrites()
	{
		// Write actions need to perform the writes synchroneously
		// If the methods are async only notifications/checks are allowed async
		
		for(IResultCommand<IFuture<Void>, Void> action: writeactions)
		{
			action.execute(null);
		}
		writeactions.clear();
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
	protected Iterator<IService> getServices(ClassInfo type)
	{
		return delegate.getServices(type);
	}

	/**
	 *  Add an excluded component. 
	 *  @param The component identifier.
	 */
	public void addExcludedComponent(IComponentIdentifier cid)
	{
		lock();
		delegate.addExcludedComponent(cid);
		unlock();
	}

	/**
	 *  Remove an excluded component. 
	 *  @param The component identifier.
	 */
	public IFuture<Void> removeExcludedComponent(IComponentIdentifier cid)
	{
		lock();
		IFuture<Void> ret = delegate.removeExcludedComponent(cid);
		unlock();
		return ret;
	}

	/**
	 *  Test if a service is included.
	 *  @param ser The service.
	 *  @return True if is included.
	 */
	public boolean isIncluded(IComponentIdentifier cid, IService ser)
	{
		lock();
		boolean ret = delegate.isIncluded(cid, ser);
		unlock();
		return ret;
	}
	
	/**
	 *  todo: WARNING: dangerous method that exposes the internal data structure
	 *  Get the service map.
	 *  @return The full service map.
	 */
	public Map<ClassInfo, Set<IService>> getServiceMap()
	{
		lock();
		Map<ClassInfo, Set<IService>> ret = delegate.getServiceMap();
		unlock();
		return ret;
	}

	/**
	 *  Get queries per type.
	 *  @param type The interface type. If type is null all services are returned.
	 *  @return The queries.
	 */
	public <T> Set<ServiceQueryInfo<T>> getQueries(ClassInfo type)
	{
		lock();
		Set<ServiceQueryInfo<T>> ret = delegate.getQueries(type);
		unlock();
		return ret;
	}

	/**
	 *  Add an event listener.
	 *  @param listener The listener.
	 */
	public void addEventListener(IRegistryListener listener)
	{
		lock();
		delegate.addEventListener(listener);
		unlock();
	}

	/**
	 *  Remove an event listener.
	 *  @param listener The listener.
	 */
	public void removeEventListener(IRegistryListener listener)
	{
		lock();
		delegate.removeEventListener(listener);
		unlock();
	}

	/**
	 *  Get a subregistry.
	 *  @param cid The platform id.
	 *  @return The registry.
	 */
	public AbstractServiceRegistry getSubregistry(IComponentIdentifier cid)
	{
		lock();
		AbstractServiceRegistry ret = delegate.getSubregistry(cid);
		unlock();
		return ret;
	}
}
