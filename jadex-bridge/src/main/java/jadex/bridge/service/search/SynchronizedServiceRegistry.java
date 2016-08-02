package jadex.bridge.service.search;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jadex.base.PlatformConfiguration;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.remote.IProxyAgentService;
import jadex.bridge.service.types.remote.IRemoteServiceManagementService;
import jadex.commons.IAsyncFilter;
import jadex.commons.IFilter;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.TerminableIntermediateFuture;

/**
 *  Local service registry used by a platform.
 *  
 *  - Makes externally used methods synchronized.
 *  - Adds methods for searching globally for services
 *  (Uses RMS getServiceProxies to retrieve remote services)
 */
public class SynchronizedServiceRegistry extends ServiceRegistry
{
	//-------- methods --------
	
	/**
	 *  Add an excluded component. 
	 *  @param The component identifier.
	 */
	public synchronized void addExcludedComponent(IComponentIdentifier cid)
	{
		super.addExcludedComponent(cid);
	}
	
	/**
	 *  Remove an excluded component. 
	 *  @param The component identifier.
	 */
	public synchronized IFuture<Void> removeExcludedComponent(IComponentIdentifier cid)
	{
		return super.removeExcludedComponent(cid);
	}
	
	/**
	 *  Test if a service is included.
	 *  @param ser The service.
	 *  @return True if is included.
	 */
	public synchronized boolean isIncluded(IComponentIdentifier cid, IService ser)
	{
		return super.isIncluded(cid, ser); 
	}
	
	/**
	 *  Add a service to the registry.
	 *  @param sid The service id.
	 */
	public synchronized IFuture<Void> addService(ClassInfo key, IService service)
	{
		return super.addService(key, service);
	}
	
	/**
	 *  Remove a service from the registry.
	 *  @param sid The service id.
	 */
	public synchronized void removeService(ClassInfo key, IService service)
	{
		super.removeService(key, service);
	}
	
	/**
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	public synchronized <T> ISubscriptionIntermediateFuture<T> addQuery(final ServiceQuery<T> query)
	{
		return super.addQuery(query);
	}
	
	/**
	 *  Remove a service query from the registry.
	 *  @param query ServiceQuery.
	 */
	public synchronized <T> void removeQuery(ServiceQuery<T> query)
	{
		super.removeQuery(query);
	}
	
	/**
	 *  Check the persistent queries for a new service.
	 *  @param ser The service.
	 */
	protected synchronized IFuture<Void> checkQueries(IService ser)
	{
		return super.checkQueries(ser);
	}
	
	/**
	 *  Check the persistent queries against a new service.
	 *  @param it The queries.
	 *  @param service the service.
	 */
	protected synchronized IFuture<Void> checkQueriesLoop(final Iterator<ServiceQueryInfo<?>> it, final IService service)
	{
		return super.checkQueriesLoop(it, service);
	}
	
	/**
	 *  Check a persistent query with one service.
	 *  @param queryinfo The query.
	 *  @param service The service.
	 *  @return True, if services matches to query.
	 */
	protected synchronized IFuture<Boolean> checkQuery(final ServiceQueryInfo<?> queryinfo, final IService service)
	{
		return super.checkQuery(queryinfo, service);
	}
	
	/**
	 *  Remove all service queries of a specific component from the registry.
	 *  @param owner The query owner.
	 */
	public synchronized void removeQueries(IComponentIdentifier owner)
	{
		super.removeQueries(owner);
	}
	
	/**
	 *  Search for services.
	 */
	public synchronized <T> T searchService(Class<T> type, IComponentIdentifier cid, String scope)
	{
		return super.searchService(type, cid, scope);
	}
	
	/**
	 *  Search for services.
	 */
	public synchronized <T> Collection<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope)
	{
		return super.searchServices(type, cid, scope);
	}
	
	/**
	 *  Search for service.
	 */
	public synchronized <T> T searchService(Class<T> type, IComponentIdentifier cid, String scope, IFilter<T> filter)
	{
		return super.searchService(type, cid, scope, filter);
	}
	
	/**
	 *  Search for service.
	 */
	public synchronized <T> Collection<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope, IFilter<T> filter)
	{
		return super.searchServices(type, cid, scope, filter);
	}
	
	/**
	 *  Search for service.
	 */
	public synchronized <T> IFuture<T> searchService(Class<T> type, IComponentIdentifier cid, String scope, IAsyncFilter<T> filter)
	{
		return super.searchService(type, cid, scope, filter);
	}
	
	/**
	 *  Perform the search in a loop.
	 *  @param filter The filter.
	 *  @param it The iterator.
	 *  @param cid The component id.
	 *  @param scope The scope.
	 */
	protected synchronized <T> IFuture<T> searchLoopService(final IAsyncFilter<T> filter, final Iterator<T> it, final IComponentIdentifier cid, final String scope)
	{
		return super.searchLoopService(filter, it, cid, scope);
	}
	
	/**
	 *  Search for services.
	 */
	public synchronized <T> ISubscriptionIntermediateFuture<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope, IAsyncFilter<T> filter)
	{
		return super.searchServices(type, cid, scope, filter);
	}
	
	/**
	 *  Perform the search in a loop.
	 */
	protected synchronized <T> ISubscriptionIntermediateFuture<T> searchLoopServices(final IAsyncFilter<T> filter, final Iterator<T> it, final IComponentIdentifier cid, final String scope)
	{
		return super.searchLoopServices(filter, it, cid, scope);
	}
	
	/**
	 *  Search for services.
	 */
	public synchronized <T> IFuture<T> searchGlobalService(final Class<T> type, IComponentIdentifier cid, final IAsyncFilter<T> filter)
	{
		final Future<T> ret = new Future<T>();
		final IComponentIdentifier	lcid	= IComponentIdentifier.LOCAL.get();
		
		searchService(type, cid, RequiredServiceInfo.SCOPE_PLATFORM, filter).addResultListener(new IResultListener<T>()
		{
			public void resultAvailable(T result)
			{
				ret.setResult(result);
			}

			public void exceptionOccurred(Exception exception)
			{
				searchRemoteService(lcid, type, filter).addResultListener(new DelegationResultListener<T>(ret));						
			}
		});
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	public synchronized <T> ITerminableIntermediateFuture<T> searchGlobalServices(Class<T> type, IComponentIdentifier cid, IAsyncFilter<T> filter)
	{
//		System.out.println("Search global services: "+type);
		final TerminableIntermediateFuture<T> ret = new TerminableIntermediateFuture<T>();
		
		final CounterResultListener<Void> lis = new CounterResultListener<Void>(2, true, new ExceptionDelegationResultListener<Void, Collection<T>>(ret)
		{
			public void customResultAvailable(Void result)
			{
				ret.setFinished();
			}
		});
		
		searchServices(type, cid, RequiredServiceInfo.SCOPE_PLATFORM, filter).addResultListener(new IntermediateDefaultResultListener<T>()
		{
			public void intermediateResultAvailable(T result)
			{
				ret.addIntermediateResult(result);
			}
			
			public void finished()
			{
				lis.resultAvailable(null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				lis.resultAvailable(null);
			}
		});
		
		searchRemoteServices(IComponentIdentifier.LOCAL.get(), type, filter).addResultListener(new IntermediateDefaultResultListener<T>()
		{
			public void intermediateResultAvailable(T result)
			{
				ret.addIntermediateResult(result);
			}
			
			public void finished()
			{
				lis.resultAvailable(null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				lis.resultAvailable(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Search for services on remote platforms.
	 *  @param caller	The component that started the search.
	 *  @param type The type.
	 *  @param filter The filter.
	 */
	protected <T> ITerminableIntermediateFuture<T> searchRemoteServices(final IComponentIdentifier caller, final Class<T> type, final IAsyncFilter<T> filter)
	{
		final TerminableIntermediateFuture<T> ret = new TerminableIntermediateFuture<T>();
		// Must not find services twice (e.g. having two proxies for the same platform)
		final Set<T> founds = new HashSet<T>();
		
		if(services!=null)
		{
			final IRemoteServiceManagementService rms = getService(IRemoteServiceManagementService.class);
			if(rms!=null)
			{
				// Get all proxy agents (represent other platforms)
				
				Set<IService> sers = services.get(new ClassInfo(IProxyAgentService.class));
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
								
								IFuture<Collection<T>> rsers = rms.getServiceProxies(caller, rcid, type, RequiredServiceInfo.SCOPE_PLATFORM, filter);
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
		}
		else
		{
			ret.setFinished();
		}
		
//		ret.addResultListener(new IntermediateDefaultResultListener<T>()
//		{
//			public void intermediateResultAvailable(T result)
//			{
//				System.out.println("found: "+result);
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("ex: "+exception);
//			}
//		});
		
		return ret;
	}
	
	/**
	 *  Search for services on remote platforms.
	 *  @param type The type.
	 *  @param scope The scope.
	 */
	protected <T> IFuture<T> searchRemoteService(final IComponentIdentifier caller, final Class<T> type, final IAsyncFilter<T> filter)
	{
		final Future<T> ret = new Future<T>();
		
		if(services!=null)
		{
			final IRemoteServiceManagementService rms = getService(IRemoteServiceManagementService.class);
			if(rms!=null)
			{
				Iterator<IService> sers = getServices(IProxyAgentService.class);
				if(sers!=null && sers.hasNext())
				{
					Set<IService> smap = getServiceMap().get(IProxyAgentService.class);
					int size = smap==null? 0: smap.size();
					
					final CounterResultListener<Void> clis = new CounterResultListener<Void>(size,
						new ExceptionDelegationResultListener<Void, T>(ret)
					{
						public void customResultAvailable(Void result)
						{
							ret.setExceptionIfUndone(new ServiceNotFoundException(type.getName()));
						}
					});
					
					while(sers.hasNext())
					{
						IService ser = sers.next();
						
						IProxyAgentService ps = (IProxyAgentService)ser;
						
						ps.getRemoteComponentIdentifier().addResultListener(new IResultListener<ITransportComponentIdentifier>()
						{
							public void resultAvailable(ITransportComponentIdentifier rcid)
							{
								IFuture<T> rsers = rms.getServiceProxy(caller, rcid, type, RequiredServiceInfo.SCOPE_PLATFORM, filter);
								rsers.addResultListener(new IResultListener<T>()
								{
									public void resultAvailable(T result)
									{
										ret.setResultIfUndone(result);
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
					ret.setExceptionIfUndone(new ServiceNotFoundException(type.getName()));
				}
			}
			else
			{
				ret.setExceptionIfUndone(new ServiceNotFoundException(type.getName()));
			}
		}
		else
		{
			ret.setExceptionIfUndone(new ServiceNotFoundException(type.getName()));
		}
		
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
}
