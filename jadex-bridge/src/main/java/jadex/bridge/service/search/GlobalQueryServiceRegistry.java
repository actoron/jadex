package jadex.bridge.service.search;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.ServiceRegistry.UnlimitedIntermediateDelegationResultListener;
import jadex.bridge.service.types.remote.IProxyAgentService;
import jadex.bridge.service.types.remote.IRemoteServiceManagementService;
import jadex.commons.IAsyncFilter;
import jadex.commons.IFilter;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DuplicateRemovalIntermediateResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.transformation.annotations.Classname;

/**
 *  Registry that allows for adding global queries with local registry.
 *  Uses remote searches to emulate the persistent query.
 */
public class GlobalQueryServiceRegistry implements IServiceRegistry
{
	/** The timer. */
	protected Timer timer;
	
	/** The global query delay. */
	protected long delay;
	
	/** The local registry. */
	protected IServiceRegistry localregistry;

	/**
	 *  Create a new GlobalQueryServiceRegistry.
	 */
	public GlobalQueryServiceRegistry(long delay)
	{
		this.delay = delay;
		this.localregistry = new ServiceRegistry();
	}
	
	/**
	 *  Add a service to the registry.
	 *  @param sid The service id.
	 */
	// write
	public IFuture<Void> addService(ClassInfo key, IService service)
	{
		return localregistry.addService(key, service);
	}
	
	/**
	 *  Remove a service from the registry.
	 *  @param sid The service id.
	 */
	// write
	public void removeService(ClassInfo key, IService service)
	{
		localregistry.removeService(key, service);
	}
	
	/**
	 *  Search for services.
	 */
	public <T> T searchServiceSync(ServiceQuery<T> query)
	{
		return localregistry.searchServiceSync(query);
	}
	
	/**
	 *  Search for services.
	 */
	public <T> Collection<T> searchServicesSync(ServiceQuery<T> query)
	{
		return localregistry.searchServicesSync(query);
	}
	
	/**
	 *  Search for services.
	 */
	public <T> IFuture<T> searchServiceAsync(final ServiceQuery<T> query)
	{
		final Future<T> ret = new Future<T>();
		localregistry.searchServiceAsync(query).addResultListener(new IResultListener<T>()
		{
			public void resultAvailable(T result)
			{
				if (result == null && RequiredServiceInfo.SCOPE_GLOBAL.equals(query.getScope()))
				{
					final ITerminableIntermediateFuture<T> sfut = searchRemoteServices(query);
					sfut.addIntermediateResultListener(new IntermediateDefaultResultListener<T>()
					{
						public void intermediateResultAvailable(T result)
						{
							ret.setResult(result);
							sfut.terminate();
						};
					});
				}
				else
					ret.setResult(result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				resultAvailable(null);
			};
		});
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	public <T> ISubscriptionIntermediateFuture<T> searchServicesAsync(ServiceQuery<T> query)
	{
		SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		IIntermediateResultListener<T> reslis = new IntermediateDelegationResultListener<T>(ret)
		{
			boolean firstfinished = false;
			
			public void finished()
			{
				if (firstfinished)
					super.finished();
				else
					firstfinished = true;
			}
		};
		
		localregistry.searchServicesAsync(query).addIntermediateResultListener(reslis);
		searchRemoteServices(query).addIntermediateResultListener(reslis);
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
//	// read
	@Deprecated
	public <T> T searchService(ServiceQuery<T> query, boolean excluded)
	{
		return localregistry.searchService(query, excluded);
	}

	/**
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(final ServiceQuery<T> query)
	{
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		
		localregistry.addQuery(query).addIntermediateResultListener(new IntermediateDelegationResultListener<T>(ret));
			
		// Emulate persistent query by searching periodically
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(query.getScope()))
		{
			final DuplicateRemovalIntermediateResultListener<T> lis = new DuplicateRemovalIntermediateResultListener<T>(new UnlimitedIntermediateDelegationResultListener<T>(ret))
			{
				public byte[] objectToByteArray(Object service)
				{
					return super.objectToByteArray(((IService)service).getServiceIdentifier());
				}
			};
			
			waitForDelay(delay, new Runnable()
			{
				public void run()
				{
//					Class<T> mytype = query.getType()==null? null: (Class<T>)query.getType().getType0();
//					searchRemoteServices(query.getOwner(), mytype, query.getFilter()).addIntermediateResultListener(lis);
//					searchRemoteServices(query.getOwner(), query.getType(), (IAsyncFilter<T>) query.getFilter()).addIntermediateResultListener(lis);
					searchRemoteServices(query).addIntermediateResultListener(lis);
					
					if(!ret.isDone())
						waitForDelay(delay, this);
//					else
//						System.out.println("stopping global query polling: "+query);
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Remove a service query from the registry.
	 *  @param query ServiceQuery.
	 */
	// write
	public <T> void removeQuery(ServiceQuery<T> query)
	{
		localregistry.removeQuery(query);
	}
	
	/**
	 *  Remove all service queries of a specific component from the registry.
	 *  @param owner The query owner.
	 */
	// write
	public void removeQueries(IComponentIdentifier owner)
	{
		localregistry.removeQueries(owner);
	}
	
	/**
	 *  Add an excluded component. 
	 *  @param The component identifier.
	 */
	// write
	public void addExcludedComponent(IComponentIdentifier cid)
	{
		localregistry.addExcludedComponent(cid);
	}
	
	/**
	 *  Remove an excluded component. 
	 *  @param The component identifier.
	 */
	// write
	public IFuture<Void> removeExcludedComponent(IComponentIdentifier cid)
	{
		return localregistry.removeExcludedComponent(cid);
	}
	
	/**
	 *  Test if a service is included.
	 *  @param ser The service.
	 *  @return True if is included.
	 */
	// read
	public boolean isIncluded(IComponentIdentifier cid, IService ser)
	{
		return localregistry.isIncluded(cid, ser);
	}
	
	/**
	 *  Search for services on remote platforms.
	 *  @param caller	The component that started the search.
	 *  @param type The type.
	 *  @param filter The filter.
	 */
	protected <T> ISubscriptionIntermediateFuture<T> searchRemoteServices(final ServiceQuery<T> query)
	{
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		// Must not find services twice (e.g. having two proxies for the same platform)
		final Set<T> founds = new HashSet<T>();
		
		final IRemoteServiceManagementService rms = getLocalServiceByClass(new ClassInfo(IRemoteServiceManagementService.class));
		if(rms!=null)
		{
			// Get all proxy agents (represent other platforms)
			Collection<IService> sers = getLocalServicesByClass(new ClassInfo(IProxyAgentService.class));
			if(sers!=null && sers.size()>0)
			{
				final CounterResultListener<Void> clis = new CounterResultListener<Void>(sers.size(), new ExceptionDelegationResultListener<Void, Collection<T>>(ret)
				{
					public void customResultAvailable(Void result)
					{
						ret.setFinished();
					}
				});
				
				for(IService ser: sers)
				{
					IProxyAgentService ps = (IProxyAgentService)ser;
					
					ps.getRemoteComponentIdentifier().addResultListener(new IResultListener<ITransportComponentIdentifier>()
					{
						public void resultAvailable(ITransportComponentIdentifier rcid)
						{
							// User RMS getServiceProxies() to fetch services
							
							final Object qfilter = query.getFilter();
							@SuppressWarnings({ "unchecked", "rawtypes" })
							IAsyncFilter<T> filter = qfilter instanceof IFilter ? new IAsyncFilter<T> ()
							{
								@Classname("GlobalQueryFilterWrapper")
								public IFuture<Boolean> filter(T obj)
								{
									return new Future<Boolean>(((IFilter<T>) qfilter).filter(obj));
								};
							} : (IAsyncFilter) qfilter;
							
							IFuture<Collection<T>> rsers = rms.getServiceProxies(query.getProvider(), rcid, query.getServiceType(), RequiredServiceInfo.SCOPE_PLATFORM, filter);
							rsers.addResultListener(new IResultListener<Collection<T>>()
							{
								public void resultAvailable(Collection<T> result)
								{
									for(T t: result)
									{
										if(!founds.contains(t))
										{
											ret.addIntermediateResult(t);
										}
										founds.add(t);
									}
									clis.resultAvailable(null);
								}
								
								public void exceptionOccurred(Exception exception)
								{
									clis.resultAvailable(null);
								}
							});
						}
						
						public void exceptionOccurred(Exception exception)
						{
							clis.resultAvailable(null);
						}
					});
				}
			}
			else
			{
				ret.setFinished();					
			}
		}
		else
		{
			ret.setFinished();
		}
		
		return ret;
	}
	
	/**
	 *  Searches for a service by class in local registry.
	 */
	protected <T> T getLocalServiceByClass(ClassInfo clazz)
	{
		// Use global scope to avoid checks on the (fake) provider.
		// Scope is naturally limited by registry selection.
		ServiceQuery<T> query = new ServiceQuery<T>(clazz, RequiredServiceInfo.SCOPE_GLOBAL, null, new BasicComponentIdentifier("localsearch"), null);
		return localregistry.searchServiceSync(query);
	}
	
	/**
	 *  Searches for services by class in local registry.
	 */
	protected <T> Collection<T> getLocalServicesByClass(ClassInfo clazz)
	{
		// Use global scope to avoid checks on the (fake) provider.
		// Scope is naturally limited by registry selection.
		ServiceQuery<T> query = new ServiceQuery<T>(clazz, RequiredServiceInfo.SCOPE_GLOBAL, null, new BasicComponentIdentifier("localsearch"), null);
		return localregistry.searchServicesSync(query);
	}
	
	/**
	 *  Wait for delay and execute runnable.
	 */
	protected void waitForDelay(long delay, final Runnable run)
	{
		if(timer==null)
			timer = new Timer(true);
		timer.schedule(new TimerTask()
		{
			public void run()
			{
				run.run();
			}
		}, delay);
	}
}
