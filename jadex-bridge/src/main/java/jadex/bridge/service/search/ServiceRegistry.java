package jadex.bridge.service.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.registry.IRegistryListener;
import jadex.bridge.service.types.registry.RegistryListenerEvent;
import jadex.bridge.service.types.remote.IProxyAgentService;
import jadex.bridge.service.types.remote.IRemoteServiceManagementService;
import jadex.commons.IAsyncFilter;
import jadex.commons.IFilter;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.commons.future.TerminationCommand;

/**
 *  Local service registry. 
 *  
 *  - Search fetches services by types and excludes some according to the scope. 
 *  - Allows for adding persistent queries.
 */
public class ServiceRegistry extends AbstractServiceRegistry
{
	//-------- attributes --------
	
	/** The map of published services sorted by type. */
	protected Map<ClassInfo, Set<IService>> services;
	
	/** The excluded components. */
	protected Set<IComponentIdentifier> excluded;
	
	/** The persistent service queries. */
	protected Map<ClassInfo, Set<ServiceQueryInfo<?>>> queries;
	
	/** The excluded services cache. */
	protected Map<IComponentIdentifier, Set<IService>> excludedservices;
	
	/** The registry listeners. */
	protected List<IRegistryListener> listeners;
	
	//-------- methods --------
	
	/**
	 *  Get a service per type.
	 *  @param type The interface type.
	 *  @return First matching service or null.
	 */
	protected <T> T getService(Class<T> type)
	{
		Set<T> sers = services==null? null: (Set<T>)services.get(new ClassInfo(type));
		return sers==null || sers.size()==0? null: (T)sers.iterator().next();
	}

	/**
	 *  Get services per type.
	 *  @param type The interface type. If type is null all services are returned.
	 *  @return First matching service or null.
	 */
	public Iterator<IService> getServices(ClassInfo type)
	{
		Set<IService> ret = null;
		
		if(services!=null)
		{
			if(type!=null)
			{
				ret = services.get(type);
			}
			else
			{
				// Return all if type is null
				ret = new HashSet<IService>();
				for(ClassInfo t: services.keySet())
				{
					ret.addAll(services.get(t));
				}
			}
		}
		
		return ret==null? null: ret.iterator();
	}
	
	/**
	 *  Test if a service is included.
	 *  @param ser The service.
	 *  @return True if is included.
	 */
	public boolean isIncluded(IComponentIdentifier cid, IService ser)
	{
		boolean ret = true;
		if(excluded!=null && excluded.contains(ser.getServiceIdentifier().getProviderId()) && cid!=null)
		{
			IComponentIdentifier target = ser.getServiceIdentifier().getProviderId();
			ret = getDotName(cid).endsWith(getDotName(target));
		}
		return ret;
	}
	
	/**
	 *  Add an excluded component. 
	 *  @param The component identifier.
	 */
	public void addExcludedComponent(IComponentIdentifier cid)
	{
		if(excluded==null)
			excluded = new HashSet<IComponentIdentifier>();
		excluded.add(cid);
	}
	
	/**
	 *  Remove an excluded component. 
	 *  @param The component identifier.
	 */
	public IFuture<Void> removeExcludedComponent(IComponentIdentifier cid)
	{
		Future<Void> ret = new Future<Void>();
		CounterResultListener<Void> lis = null;
		
//		System.out.println("cache size: "+excludedservices==null? "0":excludedservices.size());
		
		if(excluded!=null)
		{
			if(excluded.remove(cid))
			{
				Set<IService> exs = excludedservices.remove(cid);
				
				// Notify queries that new services are available
				// Must iterate over all services :-( todo: add index?
				if(queries!=null && queries.size()>0)
				{
					if(excludedservices!=null)
					{
						// Get and remove services from cache
						if(exs!=null)
						{
							lis = new CounterResultListener<Void>(exs.size(), 
								new DelegationResultListener<Void>(ret));
							for(IService ser: exs)
							{
								checkQueries(ser).addResultListener(lis);
							}
						}
					}
					
//					bar = new FutureBarrier<Void>();
//					
//					for(Set<IService> sers: services.values())
//					{
//						for(IService ser: sers)
//						{
//							if(ser.getServiceIdentifier().getProviderId().equals(cid))
//							{
//								bar.addFuture(checkQueries(ser));
//							}
//						}
//					}
//					
//					bar.waitFor().addResultListener(new DelegationResultListener<Void>(ret));;
				}
			}
		}
		
		if(lis==null)
			ret.setResult(null);
		
		return ret;
	}
	
	/**
	 *  Add a service to the registry.
	 *  @param sid The service id.
	 */
	public IFuture<Void> addService(ClassInfo key, IService service)
	{
//		if(service.getServiceIdentifier().getServiceType().getTypeName().indexOf("ITest")!=-1)
//			System.out.println("added: "+service.getServiceIdentifier().getServiceType()+" - "+service.getServiceIdentifier().getProviderId());
		
		if(services==null)
			services = new HashMap<ClassInfo, Set<IService>>();
		
		Set<IService> sers = services.get(key);
		if(sers==null)
		{
			sers = new HashSet<IService>();
			services.put(key, sers);
		}
		
		sers.add(service);
		
		// If services belongs to excluded component cache them
		IComponentIdentifier cid = service.getServiceIdentifier().getProviderId();
		if(excluded!=null && excluded.contains(cid))
		{
			if(excludedservices==null)
				excludedservices = new HashMap<IComponentIdentifier, Set<IService>>();
			Set<IService> exsers = excludedservices.get(cid);
			if(exsers==null)
			{
				exsers = new HashSet<IService>();
				excludedservices.put(cid, exsers);
			}
			exsers.add(service);
		}
		
		notifyListeners(new RegistryListenerEvent(RegistryListenerEvent.Type.ADDED, key, service));

//		System.out.println("sers: "+services.size());
		
		return checkQueries(service);
	}
	
	/**
	 *  Remove a service from the registry.
	 *  @param sid The service id.
	 */
	public void removeService(ClassInfo key, IService service)
	{
//		if(service.getServiceIdentifier().getServiceType().getTypeName().indexOf("ITest")!=-1)
//			System.out.println("removed: "+service.getServiceIdentifier().getServiceType()+" - "+service.getServiceIdentifier().getProviderId());
		
		if(services!=null)
		{
			Set<IService> sers = services.get(key);
			if(sers!=null)
			{
				sers.remove(service);
				
				notifyListeners(new RegistryListenerEvent(RegistryListenerEvent.Type.REMOVED, key, service));
			}
			else
			{
				System.out.println("Could not remove service from registry: "+key+", "+service.getServiceIdentifier());
			}
		}
		else
		{
			System.out.println("Could not remove service from registry: "+key+", "+service.getServiceIdentifier());
		}
	}
	
	/**
	 *  Search for services.
	 */
	public <T> T searchService(Class<T> type, IComponentIdentifier cid, String scope)
	{
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
			throw new IllegalArgumentException("For global searches async method searchGlobalService has to be used.");
		return super.searchService(type, cid, scope);
	}
	
	/**
	 *  Search for services.
	 */
	public <T> Collection<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope)
	{
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
			throw new IllegalArgumentException("For global searches async method searchGlobalServices has to be used.");
		return super.searchServices(type, cid, scope);
	}
	
	/**
	 *  Search for service.
	 */
	public <T> T searchService(Class<T> type, IComponentIdentifier cid, String scope, IFilter<T> filter)
	{
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
			throw new IllegalArgumentException("For global searches async method searchGlobalService has to be used.");
		return super.searchService(type, cid, scope, filter);
	}
	
	/**
	 *  Search for service.
	 */
	public <T> Collection<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope, IFilter<T> filter)
	{
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
			throw new IllegalArgumentException("For global searches async method searchGlobalService has to be used.");
		return super.searchServices(type, cid, scope, filter);
	}
	
	/**
	 *  Search for service.
	 */
	public <T> IFuture<T> searchService(Class<T> type, IComponentIdentifier cid, String scope, IAsyncFilter<T> filter)
	{
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
			throw new IllegalArgumentException("For global searches async method searchGlobalService has to be used.");
		return super.searchService(type, cid, scope, filter);
	}
	
	/**
	 *  Search for services.
	 */
	public <T> ISubscriptionIntermediateFuture<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope, IAsyncFilter<T> filter)
	{
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
			throw new IllegalArgumentException("For global searches async method searchGlobalService has to be used.");
		return super.searchServices(type, cid, scope, filter);
	}
	
	/**
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(final ServiceQuery<T> query)
	{
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		
		ret.setTerminationCommand(new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
				removeQuery(query);
			}
		});
		
		if(queries==null)
			queries = new HashMap<ClassInfo, Set<ServiceQueryInfo<?>>>();
		
		Set<ServiceQueryInfo<T>> mqs = (Set)queries.get(query.getType());
		if(mqs==null)
		{
			mqs = new HashSet<ServiceQueryInfo<T>>();
			queries.put(query.getType(), (Set)mqs);
		}
		mqs.add(new ServiceQueryInfo(query, ret));
		
		// deliver currently available services
		Set<T> sers = (Set<T>)getServices(query.getType());
		if(sers!=null)
		{
			searchLoopServices(query.getFilter(), sers.iterator(), query.getOwner(), query.getScope())
				.addIntermediateResultListener(new IIntermediateResultListener<T>()
			{
				public void intermediateResultAvailable(T result)
				{
					ret.addIntermediateResultIfUndone(result);
				}
	
				public void finished()
				{
					// the query is not finished after the status quo is delivered
				}
	
				public void resultAvailable(Collection<T> results)
				{
					for(T result: results)
					{
						intermediateResultAvailable(result);
					}
					// the query is not finished after the status quo is delivered
				}
				
				public void exceptionOccurred(Exception exception)
				{
					// the query is not finished after the status quo is delivered
				}
			});
		}
	
		return ret;
	}
	
	/**
	 *  Remove a service query from the registry.
	 *  @param query ServiceQuery.
	 */
	public <T> void removeQuery(ServiceQuery<T> query)
	{
		if(queries!=null)
		{
			Set<ServiceQuery<T>> mqs = (Set)queries.get(query.getType());
			if(mqs!=null)
			{
				mqs.remove(query);
				if(mqs.size()==0)
					queries.remove(query.getType());
			}
		}
	}
	
	/**
	 *  Remove all service queries of a specific component from the registry.
	 *  @param owner The query owner.
	 */
	public void removeQueries(IComponentIdentifier owner)
	{
		if(queries!=null)
		{
			for(Map.Entry<ClassInfo, Set<ServiceQueryInfo<?>>> entry: queries.entrySet())
			{
				for(ServiceQueryInfo<?> query: entry.getValue())
				{
					if(owner.equals(query.getQuery().getOwner()))
					{
						entry.getValue().remove(query);
					}
				}
			}
		}
	}
	
	/**
	 *  Get queries per type.
	 *  @param type The interface type. If type is null all services are returned.
	 *  @return The queries.
	 */
	public <T> Set<ServiceQueryInfo<T>> getQueries(ClassInfo type)
	{
		return queries==null? Collections.EMPTY_SET: queries.get(type);
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
	public <T> ITerminableIntermediateFuture<T> searchGlobalServices(Class<T> type, IComponentIdentifier cid, IAsyncFilter<T> filter)
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
	 *  Get a subregistry.
	 *  @param cid The platform id.
	 *  @return The registry.
	 */
	public AbstractServiceRegistry getSubregistry(IComponentIdentifier cid)
	{
		return null;
	}
	
	/**
	 *  Get the service map.
	 */
	public Map<ClassInfo, Set<IService>> getServiceMap()
	{
		return services;
	}
	
	/**
	 *  Check if service is ok with respect to search scope of caller.
	 */
	protected boolean checkSearchScope(IComponentIdentifier cid, IService ser, String scope)
	{
		if(!isIncluded(cid, ser))
		{
			return false;
		}
		else
		{
			return super.checkSearchScope(cid, ser, scope);
		}
	}

	/**
	 *  Notify the event listeners (if any).
	 *  @param event The event.
	 */
	protected void notifyListeners(RegistryListenerEvent event)
	{
		if(listeners!=null)
		{
			for(IRegistryListener listener: listeners)
			{
				listener.registryChanged(event);
			}
		}
	}
	
	/**
	 *  Add an event listener.
	 *  @param listener The listener.
	 */
	public void addEventListener(IRegistryListener listener)
	{
		if(listeners==null)
			listeners = new ArrayList<IRegistryListener>();
		listeners.add(listener);
	}
	
	/**
	 *  Remove an event listener.
	 *  @param listener The listener.
	 */
	public void removeEventListener(IRegistryListener listener)
	{
		if(listeners!=null)
			listeners.remove(listener);
	}
	
}
