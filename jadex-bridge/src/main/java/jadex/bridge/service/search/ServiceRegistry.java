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

import jadex.base.PlatformConfiguration;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.registry.IRegistryListener;
import jadex.bridge.service.types.registry.RegistryListenerEvent;
import jadex.commons.IAsyncFilter;
import jadex.commons.ICommand;
import jadex.commons.IFilter;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;

/**
 *  Local service registry. 
 *  
 *  - Search fetches services by types and excludes some according to the scope. 
 *  - Allows for adding persistent queries.
 */
public class ServiceRegistry implements IServiceRegistry // extends AbstractServiceRegistry
{
	//-------- attributes --------
	
	/** The map of published services sorted by type. */
//	protected Map<ClassInfo, Set<IService>> services;
	
	/** The service indexer. */
	protected ServiceIndexer<IService> indexer;
	
	/** The excluded components. */
//	protected Set<IComponentIdentifier> excluded;
	
	/** The excluded services cache. */
	protected Map<IComponentIdentifier, Set<IService>> excludedservices;
	
	/** The registry listeners. */
	protected List<IRegistryListener> listeners;
	
	/** The persistent service queries. */
	protected Map<ClassInfo, Set<ServiceQueryInfo<?>>> queries;
	
	//-------- methods --------
	
	/**
	 *  Create a new registry.
	 */
	public ServiceRegistry()//RegistrySearchFunctionality searchfunc)
	{
		this.indexer = new ServiceIndexer<IService>(new JadexServiceKeyExtractor(), JadexServiceKeyExtractor.SERVICE_KEY_TYPES);
	}
	
	/**
	 *  Get a service per type.
	 *  @param type The interface type.
	 *  @return First matching service or null.
	 */
//	protected <T> T getService(Class<T> type)
//	{
//		Set<T> sers = services==null? null: (Set<T>)services.get(new ClassInfo(type));
//		return sers==null || sers.size()==0? null: (T)sers.iterator().next();
//	}
	
	/**
	 *  Get all services.
	 *  @return The services.
	 */
	public Set<IService> getServices()
	{
		return indexer.getAllServices();
	}

	/**
	 *  Get services per query.
	 *  @param query The query.
	 *  @return First matching service or null.
	 */
	public Set<IService> getServices(ServiceQuery<?> query)
	{
		Set<IService> ret = indexer.getServices(query.getIndexerSearchSpec());
		return ret;
	}
	
	/**
	 *  Test if a service is included.
	 *  @param ser The service.
	 *  @return True if is included.
	 */
	public boolean isIncluded(IComponentIdentifier cid, IService ser)
	{
		boolean ret = true;
		if(excludedservices!=null && ser == null && cid != null && excludedservices.containsKey(cid))
		{
			ret = false;
		}
		else if(excludedservices!=null && excludedservices.containsKey(ser.getServiceIdentifier().getProviderId()) && cid!=null)
		{
			IComponentIdentifier target = ser.getServiceIdentifier().getProviderId();
			if(target!=null)
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
//		if(excluded==null)
//			excluded = new HashSet<IComponentIdentifier>();
		excludedservices.put(cid, null);
	}
	
	/**
	 *  Remove an excluded component. 
	 *  @param The component identifier.
	 */
	public IFuture<Void> removeExcludedComponent(IComponentIdentifier cid)
	{
//		System.out.println("cache size: "+excludedservices==null? "0":excludedservices.size());
		
		Future<Void> ret = new Future<Void>();
		IResultListener<Void> lis = null;
		
		if(excludedservices!=null)
		{
			Set<IService> exs = excludedservices.remove(cid);
			
			if(queries!=null && queries.size()>0)
			{
				// Get and remove services from cache
				if(exs!=null)
				{
					lis = new CounterResultListener<Void>(exs.size(), new DelegationResultListener<Void>(ret));
					for(IService ser: exs)
					{
						checkQueries(ser, false).addResultListener(lis);
					}
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
//		if(service.getServiceIdentifier().getServiceType().getTypeName().indexOf("IRegistrySer")!=-1)
//			System.out.println("added: "+service.getServiceIdentifier().getServiceType()+" - "+service.getServiceIdentifier().getProviderId());
			
//		if(services==null)
//			services = new HashMap<ClassInfo, Set<IService>>();
//		
//		Set<IService> sers = services.get(key);
//		if(sers==null)
//		{
//			sers = new HashSet<IService>();
//			services.put(key, sers);
//		}
//		
//		sers.add(service);
		
		indexer.addService(service);
		
		// If services belongs to excluded component cache them
		IComponentIdentifier cid = service.getServiceIdentifier().getProviderId();
		if(excludedservices!=null && excludedservices.containsKey(cid))
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
		
		checkQueries(service, false);
		
		notifyListeners(new RegistryListenerEvent(RegistryListenerEvent.Type.ADDED, key, service));

//		System.out.println("sers: "+services.size());
		
		return IFuture.DONE;
	}
	
	/**
	 *  Remove a service from the registry.
	 *  @param sid The service id.
	 */
	public void removeService(ClassInfo key, IService service)
	{
		indexer.removeService(service);
		checkQueries(service, true);
	}
	
	/**
	 *  Search for services.
	 */
	@SuppressWarnings("unchecked")
	public <T> T searchServiceSync(ServiceQuery<T> query)
	{
		T ret = null;
		if (!RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
		{
			if (query.getFilter() instanceof IAsyncFilter)
				throw new IllegalArgumentException("Synchronous search call with asynchronous filter in query: " + query);
			
			Set<IService> sers = getServices(query);
			IFilter<T> filter = (IFilter<T>) query.getFilter();
			filter = (IFilter<T>) (filter == null? IFilter.ALWAYS : filter);
			
			if (sers!=null && !sers.isEmpty())
			{
				for (IService ser : sers)
				{
					if(checkSearchScope(query.getProvider(), ser, query.getScope(), false) &&
					   checkPublicationScope(query.getProvider(), ser) &&
					   filter.filter((T) ser))
					{
						ret = (T)ser;
						break;
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	@SuppressWarnings("unchecked")
	public <T> Collection<T> searchServicesSync(ServiceQuery<T> query)
	{
		Collection<T> ret = null;
		if (!RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
		{
			if (query.getFilter() instanceof IAsyncFilter)
				throw new IllegalArgumentException("Synchronous search call with asynchronous filter in query: " + query);
			
			Set<IService> sers = getServices(query);
			IFilter<T> filter = (IFilter<T>) query.getFilter();
			filter = (IFilter<T>) (filter == null? IFilter.ALWAYS : filter);
			
			if (sers!=null && !sers.isEmpty())
			{
				for (Iterator<IService> it = sers.iterator(); it.hasNext(); )
				{
					IService ser = it.next();
					if(!(checkSearchScope(query.getProvider(), ser, query.getScope(), false) && checkPublicationScope(query.getProvider(), ser) && filter.filter((T) ser)))
						it.remove();
				}
			}
			ret = (Collection<T>) sers;
		}
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	public <T> IFuture<T> searchServiceAsync(final ServiceQuery<T> query)
	{
		final Future<T> ret = new Future<T>();;
		
		if (RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
		{
			ret.setException(new ServiceNotFoundException(query.getServiceType() != null? query.getServiceType().getTypeName() : query.toString()));
		}
		else
		{
			Set<IService> sers = getServices(query);
			final Iterator<IService> it = sers.iterator();
			
			(new ICommand<Iterator<IService>>()
			{
				@SuppressWarnings({ "unchecked", "rawtypes" })
				public void execute(final Iterator<IService> it)
				{
					IService ser = it.next();
					
					T tmp = null;
					if (query.getReturnType() != null && query.getReturnType().getTypeName().equals(ServiceEvent.CLASSINFO.getTypeName()))
					{
						tmp = (T) new ServiceEvent(ser, ServiceEvent.SERVICE_ADDED);
					}
					final T obj = tmp;
					
					final ICommand<Iterator<IService>> cmd = this;
					
					boolean passes = checkSearchScope(query.getProvider(), ser, query.getScope(), false);
					passes &= checkPublicationScope(query.getProvider(), ser);
					if (query.getFilter() instanceof IFilter)
					{
						passes &= ((IFilter<T>) query.getFilter()).filter(obj);
					}
					
					if (passes)
					{
						if (query.getFilter() instanceof IAsyncFilter)
						{
							((IAsyncFilter<T>) query.getFilter()).filter(obj).addResultListener(new IResultListener<Boolean>()
							{
								public void resultAvailable(Boolean result)
								{
									if (Boolean.TRUE.equals(result))
										ret.setResult(obj);
									else
										exceptionOccurred(null);
								}
								
								public void exceptionOccurred(Exception exception)
								{
									if (it.hasNext())
										cmd.execute(it);
									else
										ret.setException(new ServiceNotFoundException(query.getServiceType() != null? query.getServiceType().getTypeName() : query.toString()));
								}
							});
						}
						else
							ret.setResult(obj);
					}
					else
					{
						if (it.hasNext())
							cmd.execute(it);
						else
							ret.setException(new ServiceNotFoundException(query.getServiceType() != null? query.getServiceType().getTypeName() : query.toString()));
					}
				};
			}).execute(it);
		}
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> ISubscriptionIntermediateFuture<T> searchServicesAsync(final ServiceQuery<T> query)
	{	
		SubscriptionIntermediateFuture<T> ret = null;
		
		if (RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
		{
			ret = new SubscriptionIntermediateFuture<T>();
			ret.setFinished();
		}
		else
		{
			IAsyncFilter<T> filter = new QueryFilter(query);
			Set sers = getServices(query);
			ret = (SubscriptionIntermediateFuture<T>) checkAsyncFilters(filter, sers.iterator());
		}
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	// read
	@Deprecated
	@SuppressWarnings("unchecked")
	public <T> T searchService(ServiceQuery<T> query, boolean excluded)
	{
		if (RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
			return null;
		
		T ret = null;
		Set<IService> sers = getServices(query);
		if(sers!=null)
		{
			Iterator<IService> it = sers.iterator();
			while(it.hasNext())
			{
				IService ser = it.next();
				if(checkSearchScope(query.getProvider(), ser, query.getScope(), excluded) && checkPublicationScope(query.getProvider(), ser))
				{
					ret = (T)ser;
					break;
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Search for services on remote platforms.
	 *  @param caller	The component that started the search.
	 *  @param type The type.
	 *  @param filter The filter.
	 */
//	protected <T> ITerminableIntermediateFuture<T> searchRemoteServices(final IComponentIdentifier caller, final ClassInfo type, final IAsyncFilter<T> filter)
//	{
//		final TerminableIntermediateFuture<T> ret = new TerminableIntermediateFuture<T>();
//		// Must not find services twice (e.g. having two proxies for the same platform)
//		final Set<T> founds = new HashSet<T>();
//		
//		if(services!=null)
//		{
//			final IRemoteServiceManagementService rms = getService(IRemoteServiceManagementService.class);
//			if(rms!=null)
//			{
//				// Get all proxy agents (represent other platforms)
//				
//				Set<IService> sers = services.get(new ClassInfo(IProxyAgentService.class));
//				if(sers!=null && sers.size()>0)
//				{
//					final CounterResultListener<Void> clis = new CounterResultListener<Void>(sers.size(), new ExceptionDelegationResultListener<Void, Collection<T>>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							ret.setFinished();
//						}
//					});
//					
//					for(IService ser: sers)
//					{
//						IProxyAgentService ps = (IProxyAgentService)ser;
//						
//						ps.getRemoteComponentIdentifier().addResultListener(new IResultListener<ITransportComponentIdentifier>()
//						{
//							public void resultAvailable(ITransportComponentIdentifier rcid)
//							{
//								// User RMS getServiceProxies() to fetch services
//								
//								IFuture<Collection<T>> rsers = rms.getServiceProxies(caller, rcid, type, RequiredServiceInfo.SCOPE_PLATFORM, filter);
//								rsers.addResultListener(new IResultListener<Collection<T>>()
//								{
//									public void resultAvailable(Collection<T> result)
//									{
//										for(T t: result)
//										{
//											if(!founds.contains(t))
//											{
//												ret.addIntermediateResult(t);
//											}
//											founds.add(t);
//										}
//										clis.resultAvailable(null);
//									}
//									
//									public void exceptionOccurred(Exception exception)
//									{
//										clis.resultAvailable(null);
//									}
//								});
//							}
//							
//							public void exceptionOccurred(Exception exception)
//							{
//								clis.resultAvailable(null);
//							}
//						});
//					}
//				}
//				else
//				{
//					ret.setFinished();					
//				}
//			}
//			else
//			{
//				ret.setFinished();
//			}
//		}
//		else
//		{
//			ret.setFinished();
//		}
//		
////		ret.addResultListener(new IntermediateDefaultResultListener<T>()
////		{
////			public void intermediateResultAvailable(T result)
////			{
////				System.out.println("found: "+result);
////			}
////			
////			public void exceptionOccurred(Exception exception)
////			{
////				System.out.println("ex: "+exception);
////			}
////		});
//		
//		return ret;
//	}
	
	/**
	 *  Search for services on remote platforms.
	 *  @param type The type.
	 *  @param scope The scope.
	 */
//	protected <T> IFuture<T> searchRemoteService(final IComponentIdentifier caller, final ClassInfo type, final IAsyncFilter<T> filter)
//	{
//		final Future<T> ret = new Future<T>();
//		
////		if(type.toString().indexOf("IServiceCall")!=-1)
////			System.out.println("Search global services: "+type);
//		
//		if(services!=null)
//		{
//			final IRemoteServiceManagementService rms = getService(IRemoteServiceManagementService.class);
//			if(rms!=null)
//			{
//				Iterator<IService> sers = getServices(new ClassInfo(IProxyAgentService.class));
//				if(sers!=null && sers.hasNext())
//				{
//					Set<IService> smap = getServiceMap().get(new ClassInfo(IProxyAgentService.class));
//					int size = smap==null? 0: smap.size();
//					
//					final CounterResultListener<Void> clis = new CounterResultListener<Void>(size,
//						new ExceptionDelegationResultListener<Void, T>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							ret.setExceptionIfUndone(new ServiceNotFoundException(type.getTypeName()));
//						}
//					});
//					
//					while(sers.hasNext())
//					{
//						IService ser = sers.next();
//						
//						IProxyAgentService ps = (IProxyAgentService)ser;
//						
//						ps.getRemoteComponentIdentifier().addResultListener(new IResultListener<ITransportComponentIdentifier>()
//						{
//							public void resultAvailable(ITransportComponentIdentifier rcid)
//							{
//								IFuture<T> rsers = rms.getServiceProxy(caller, rcid, type, RequiredServiceInfo.SCOPE_PLATFORM, filter);
//								rsers.addResultListener(new IResultListener<T>()
//								{
//									public void resultAvailable(T result)
//									{
//										ret.setResultIfUndone(result);
//										clis.resultAvailable(null);
//									}
//									
//									public void exceptionOccurred(Exception exception)
//									{
//										clis.resultAvailable(null);
//									}
//								});
//							}
//							
//							public void exceptionOccurred(Exception exception)
//							{
//								clis.resultAvailable(null);
//							}
//						});
//					}
//				}
//				else
//				{
//					ret.setExceptionIfUndone(new ServiceNotFoundException(type.getTypeName()));
//				}
//			}
//			else
//			{
//				ret.setExceptionIfUndone(new ServiceNotFoundException(type.getTypeName()));
//			}
//		}
//		else
//		{
//			ret.setExceptionIfUndone(new ServiceNotFoundException(type.getTypeName()));
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Get a subregistry.
	 *  @param cid The platform id.
	 *  @return The registry.
	 */
//	public IServiceRegistry getSubregistry(IComponentIdentifier cid)
//	{
//		return null;
//	}
	
	/**
	 *  Remove a subregistry.
	 *  @param cid The platform id.
	 */
	// write
//	public void removeSubregistry(IComponentIdentifier cid)
//	{
//	}
	
	/**
	 *  Get the service map. (The original map cannot be used because 
	 *  registry is accessed concurrently and other threads could change the map
	 *  even in between of onging operations such as serialization)
	 *  @return A clone of the service map.
	 */
//	public Map<ClassInfo, Set<IService>> getServiceMap()
//	{
//		// Does not work because the contained services are cloned also 
////		return services==null? null: (Map<ClassInfo, Set<IService>>)Traverser.traverseObject(services, Traverser.getDefaultProcessors(), true, null);
//	
//		// Needs a deep clone except the services
//		
//		Map<ClassInfo, Set<IService>> ret = null;
//		
//		if(services!=null)
//		{
//			ret = new HashMap<ClassInfo, Set<IService>>();
//			for(Map.Entry<ClassInfo, Set<IService>> entry: services.entrySet())
//			{
//				ret.put(entry.getKey(), new HashSet<IService>(entry.getValue()));
//			}
//		}
//		
//		return ret;
//	}
	
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
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		
		Set<ServiceQueryInfo<?>> mqs = (Set)queries.get(query.getServiceType());
		if(mqs==null)
		{
			mqs = new HashSet<ServiceQueryInfo<?>>();
			queries.put(query.getServiceType(), (Set)mqs);
		}
		mqs.add(new ServiceQueryInfo<T>(query, ret));
		
		// deliver currently available services
		Set<T> sers = (Set<T>)getServices(query);
		if(sers!=null)
		{
			IAsyncFilter<T> filter = new IAsyncFilter<T>()
			{
				public IFuture<Boolean> filter(T ser)
				{
					Future<Boolean> ret = null;
					if (!checkScope(ser, query.getProvider(), query.getScope()))
					{
						ret = new Future<Boolean>(Boolean.FALSE);
					}
					else if (query.getFilter() instanceof IAsyncFilter)
					{
						ret = (Future<Boolean>) ((IAsyncFilter) query.getFilter()).filter(ser);
					}
					else if (query.getFilter() instanceof IFilter)
					{
						ret = new Future<Boolean>(((IFilter) query.getFilter()).filter(ser));
					}
					else
					{
						ret = new Future<Boolean>(Boolean.TRUE);
					}
					
					return ret;
				}
			};
			Iterator it = sers.iterator();
			checkAsyncFilters(filter, it).addIntermediateResultListener(new UnlimitedIntermediateDelegationResultListener<T>(ret));;
		}
	
		return ret;
	}
	
	/**
	 *  Remove a service query from the registry.
	 *  @param query ServiceQuery.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> void removeQuery(ServiceQuery<T> query)
	{
		if(queries!=null)
		{
			Set<ServiceQueryInfo<?>> mqs = (Set)queries.get(query.getServiceType());
			if(mqs!=null)
			{
				for(ServiceQueryInfo<?> sqi: mqs)
				{
					if(sqi.getQuery().equals(query))
					{
						sqi.getFuture().setFinished();;
						mqs.remove(sqi);
						break;
					}
				}
				if(mqs.size()==0)
					queries.remove(query.getServiceType());
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
				for(ServiceQueryInfo<?> query: entry.getValue().toArray(new ServiceQueryInfo<?>[entry.getValue().size()]))
				{
					if(owner.equals(query.getQuery().getOwner()))
					{
						removeQuery(query.getQuery());
//						entry.getValue().remove(query);
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
	@SuppressWarnings("unchecked")
	protected <T> Set<ServiceQueryInfo<T>> getQueries(ClassInfo type)
	{
		return queries==null? Collections.EMPTY_SET: queries.get(type);
	}
	
	/**
	 *  Check the persistent queries for a new service.
	 *  @param ser The service.
	 */
	// read
	protected IFuture<Void> checkQueries(IService ser, boolean removed)
	{
		Future<Void> ret = new Future<Void>();
		
//		if(queries!=null)
//		{
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Set<ServiceQueryInfo<?>> sqis = (Set)getQueries(ser.getServiceIdentifier().getServiceType());
			
			if(sqis!=null)
			{
				// Clone the data to not need to synchronize async
				Set<ServiceQueryInfo<?>> clone = new HashSet<ServiceQueryInfo<?>>(sqis);
				
				checkQueriesLoop(clone.iterator(), ser, removed).addResultListener(new DelegationResultListener<Void>(ret));
			}
			else
			{
				ret.setResult(null);
			}
//		}
//		else
//		{
//			ret.setResult(null);
//		}
		
		return ret;
	}
	
	/**
	 *  Check the persistent queries against a new service.
	 *  @param it The queries.
	 *  @param service the service.
	 */
	// read
	protected IFuture<Void> checkQueriesLoop(final Iterator<ServiceQueryInfo<?>> it, final IService service, final boolean removed)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(it.hasNext())
		{
			final ServiceQueryInfo<?> sqi = it.next();
//			IComponentIdentifier cid = sqi.getQuery().getOwner();
//			String scope = sqi.getQuery().getScope();
//			IAsyncFilter<IService> filter = (IAsyncFilter)sqi.getQuery().getFilter();
			
			checkQuery(sqi, service, removed).addResultListener(new ExceptionDelegationResultListener<Boolean, Void>(ret)
			{
				@SuppressWarnings({ "unchecked", "rawtypes" })
				public void customResultAvailable(Boolean result) throws Exception
				{
					if(result.booleanValue())
					{
						Object ires = null;
						if (ServiceEvent.CLASSINFO.equals(sqi.getQuery().getReturnType()))
							ires = new ServiceEvent(service, removed ? ServiceEvent.SERVICE_REMOVED : ServiceEvent.SERVICE_ADDED);
						else
							ires = service;
						((IntermediateFuture)sqi.getFuture()).addIntermediateResult(ires);
					}
					checkQueriesLoop(it, service, removed).addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
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
	
	/**
	 *  Check the services according the the scope.
	 *  @param it The services.
	 *  @param cid The component id.
	 *  @param scope The scope.
	 *  @return The services that fit to the scope.
	 */
	protected <T> boolean checkScope(final T ser, final IComponentIdentifier cid, final String scope)
	{
		return checkSearchScope(cid, (IService)ser, scope, false) && checkPublicationScope(cid, (IService)ser);
	}
	
	/**
	 *  Check the async filter.
	 *  @param filter The filter
	 *  @param it The services.
	 *  @return The services that pass the filter.
	 */
	// read -> Async is error prone when lock is held longer time spans
	protected  <T> ISubscriptionIntermediateFuture<T> checkAsyncFilters(final IAsyncFilter<T> filter, final Iterator<T> it)
	{
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		
		if(it.hasNext())
		{
			final T ser = it.next();
			if(filter==null)
			{
				ret.addIntermediateResult(ser);
				checkAsyncFilters(filter, it).addResultListener(new IntermediateDelegationResultListener<T>(ret));
			}
			else
			{
				filter.filter(ser).addResultListener(new IResultListener<Boolean>()
				{
					public void resultAvailable(Boolean result)
					{
						if(result!=null && result.booleanValue())
						{
							ret.addIntermediateResult(ser);
						}
						checkAsyncFilters(filter, it).addResultListener(new IntermediateDelegationResultListener<T>(ret));
					}
					
					public void exceptionOccurred(Exception exception)
					{
						checkAsyncFilters(filter, it).addResultListener(new IntermediateDelegationResultListener<T>(ret));
					}
				});
			}
		}
		else
		{
//			System.out.println("searchLoopEnd");
			ret.setFinished();
		}
		
		return ret;
	}
	
	/**
	 *  Check a persistent query with one service.
	 *  @param queryinfo The query.
	 *  @param service The service.
	 *  @return True, if services matches to query.
	 */
	// read
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected IFuture<Boolean> checkQuery(final ServiceQueryInfo<?> queryinfo, final IService service, final boolean removed)
	{
		
		
		final Future<Boolean> ret = new Future<Boolean>();
//		IComponentIdentifier cid = queryinfo.getQuery().getOwner();
//		String scope = queryinfo.getQuery().getScope();
//		@SuppressWarnings("unchecked")
		
		if (removed && !ServiceEvent.CLASSINFO.equals(queryinfo.getQuery().getReturnType()))
			ret.setResult(Boolean.FALSE);
		else
		{
			IAsyncFilter filter = new QueryFilter(queryinfo.getQuery());
			filter.filter(service).addResultListener(new IResultListener<Boolean>()
			{
				public void resultAvailable(Boolean result)
				{
					ret.setResult(result!=null && result.booleanValue()? Boolean.TRUE: Boolean.FALSE);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ret.setResult(Boolean.FALSE);
				}
			});
		}
		
//		IAsyncFilter<IService> filter = (IAsyncFilter<IService>) (queryinfo.getQuery().getFilter() instanceof IAsyncFilter? queryinfo.getQuery().getFilter() : new AsyncSyncFilterAdapter<IService>((IFilter<IService>) queryinfo.getQuery().getFilter()));
//		if(!checkSearchScope(cid, service, scope, false) || !checkPublicationScope(cid, service))
//		{
//			ret.setResult(Boolean.FALSE);
//		}
//		else
//		{
//			if(filter==null)
//			{
//				ret.setResult(Boolean.TRUE);
//			}
//			else
//			{
//				filter.filter(service).addResultListener(new IResultListener<Boolean>()
//				{
//					public void resultAvailable(Boolean result)
//					{
//						ret.setResult(result!=null && result.booleanValue()? Boolean.TRUE: Boolean.FALSE);
//					}
//					
//					public void exceptionOccurred(Exception exception)
//					{
//						ret.setResult(Boolean.FALSE);
//					}
//				});
//			}
//		}
		
		return ret;
	}
	
	/**
	 *  Check if service is ok with respect to search scope of caller.
	 */
	protected boolean checkSearchScope(IComponentIdentifier cid, IService ser, String scope, boolean excluded)
	{
		boolean ret = false;
		
		if(!excluded && !isIncluded(cid, ser))
		{
			return ret;
		}
		
		if(scope==null)
		{
			scope = RequiredServiceInfo.SCOPE_APPLICATION;
		}
		
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
		{
			ret = true;
		}
		else if(RequiredServiceInfo.SCOPE_PLATFORM.equals(scope))
		{
			// Test if searcher and service are on same platform
			ret = cid.getPlatformName().equals(ser.getServiceIdentifier().getProviderId().getPlatformName());
		}
		else if(RequiredServiceInfo.SCOPE_APPLICATION.equals(scope))
		{
			IComponentIdentifier sercid = ser.getServiceIdentifier().getProviderId();
			ret = sercid.getPlatformName().equals(cid.getPlatformName())
				&& getApplicationName(sercid).equals(getApplicationName(cid));
		}
		else if(RequiredServiceInfo.SCOPE_COMPONENT.equals(scope))
		{
			IComponentIdentifier sercid = ser.getServiceIdentifier().getProviderId();
			ret = getDotName(sercid).endsWith(getDotName(cid));
		}
		else if(RequiredServiceInfo.SCOPE_LOCAL.equals(scope))
		{
			// only the component itself
			ret = ser.getServiceIdentifier().getProviderId().equals(cid);
		}
		else if(RequiredServiceInfo.SCOPE_PARENT.equals(scope))
		{
			// check if parent of searcher reaches the service
			IComponentIdentifier sercid = ser.getServiceIdentifier().getProviderId();
			String subname = getSubcomponentName(cid);
			ret = sercid.getName().endsWith(subname);
		}
//		else if(RequiredServiceInfo.SCOPE_UPWARDS.equals(scope))
//		{
//			// Test if service id is part of searcher id, service is upwards from searcher
//			IComponentIdentifier sercid = ser.getServiceIdentifier().getProviderId();
//			ret = getDotName(cid).endsWith(getDotName(sercid));
//			
////			IComponentIdentifier sercid = ser.getServiceIdentifier().getProviderId();
////			String subname = getSubcomponentName(cid);
////			ret = sercid.getName().endsWith(subname);
////			
////			while(cid!=null)
////			{
////				if(sercid.equals(cid))
////				{
////					ret = true;
////					break;
////				}
////				else
////				{
////					cid = cid.getParent();
////				}
////			}
//		}
		
		return ret;
	}
	
	/**
	 *  Check if service is ok with respect to publication scope.
	 */
	protected boolean checkPublicationScope(IComponentIdentifier cid, IService ser)
	{
		boolean ret = false;
		
		String scope = ser.getServiceIdentifier().getScope()!=null? ser.getServiceIdentifier().getScope(): RequiredServiceInfo.SCOPE_GLOBAL;
		
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
		{
			ret = true;
		}
		else if(RequiredServiceInfo.SCOPE_PLATFORM.equals(scope))
		{
			// Test if searcher and service are on same platform
			ret = cid.getPlatformName().equals(ser.getServiceIdentifier().getProviderId().getPlatformName());
		}
		else if(RequiredServiceInfo.SCOPE_APPLICATION.equals(scope))
		{
			// todo: special case platform service with app scope
			IComponentIdentifier sercid = ser.getServiceIdentifier().getProviderId();
			ret = sercid.getPlatformName().equals(cid.getPlatformName())
				&& getApplicationName(sercid).equals(getApplicationName(cid));
		}
		else if(RequiredServiceInfo.SCOPE_COMPONENT.equals(scope))
		{
			IComponentIdentifier sercid = ser.getServiceIdentifier().getProviderId();
			ret = getDotName(cid).endsWith(getDotName(sercid));
		}
		else if(RequiredServiceInfo.SCOPE_LOCAL.equals(scope))
		{
			// only the component itself
			ret = ser.getServiceIdentifier().getProviderId().equals(cid);
		}
		else if(RequiredServiceInfo.SCOPE_PARENT.equals(scope))
		{
			// check if parent of service reaches the searcher
			IComponentIdentifier sercid = ser.getServiceIdentifier().getProviderId();
			String subname = getSubcomponentName(sercid);
			ret = getDotName(cid).endsWith(subname);
		}
//		else if(RequiredServiceInfo.SCOPE_UPWARDS.equals(scope))
//		{
//			// check if searcher is upwards from service (part of name)
//			IComponentIdentifier sercid = ser.getServiceIdentifier().getProviderId();
//			ret = getDotName(sercid).endsWith(getDotName(cid));
//		}
		
		return ret;
	}
	
	/**
	 *  Get the registry from a component.
	 */
	public static IServiceRegistry getRegistry(IComponentIdentifier platform)
	{
		return (IServiceRegistry)PlatformConfiguration.getPlatformValue(platform, PlatformConfiguration.DATA_SERVICEREGISTRY);
	}
	
	/**
	 *  Get the registry from a component.
	 */
	public static IServiceRegistry getRegistry(IInternalAccess ia)
	{
		return getRegistry(ia.getComponentIdentifier());
	}
	
	/**
	 *  Get the application name. Equals the local component name in case it is a child of the platform.
	 *  broadcast@awa.plat1 -> awa
	 *  @return The application name.
	 */
	public static String getApplicationName(IComponentIdentifier cid)
	{
		String ret = cid.getName();
		int idx;
		// If it is a direct subcomponent
		if((idx = ret.lastIndexOf('.')) != -1)
		{
			// cut off platform name
			ret = ret.substring(0, idx);
			// cut off local name 
			if((idx = ret.indexOf('@'))!=-1)
				ret = ret.substring(idx + 1);
			if((idx = ret.indexOf('.'))!=-1)
				ret = ret.substring(idx + 1);
		}
		else
		{
			ret = cid.getLocalName();
		}
		return ret;
	}
	
	/**
	 *  Get the subcomponent name.
	 *  @param cid The component id.
	 *  @return The subcomponent name.
	 */
	public static String getSubcomponentName(IComponentIdentifier cid)
	{
		String ret = cid.getName();
		int idx;
		if((idx = ret.indexOf('@'))!=-1)
			ret = ret.substring(idx + 1);
		return ret;
	}
	
	/**
	 *  Get the name without @ replaced by dot.
	 */
	public static String getDotName(IComponentIdentifier cid)
	{
		return cid.getName().replace('@', '.');
//		return cid.getParent()==null? cid.getName(): cid.getLocalName()+"."+getSubcomponentName(cid);
	}
	
	/**
	 *  Listener that forwards only results and ignores finished / exception.
	 */
	public static class UnlimitedIntermediateDelegationResultListener<E> implements IIntermediateResultListener<E>
	{
		/** The delegate future. */
		protected IntermediateFuture<E> delegate;
		
		public UnlimitedIntermediateDelegationResultListener(IntermediateFuture<E> delegate)
		{
			this.delegate = delegate;
		}
		
		public void intermediateResultAvailable(E result)
		{
			delegate.addIntermediateResultIfUndone(result);
		}

		public void finished()
		{
			// the query is not finished after the status quo is delivered
		}

		public void resultAvailable(Collection<E> results)
		{
			for(E result: results)
			{
				intermediateResultAvailable(result);
			}
			// the query is not finished after the status quo is delivered
		}
		
		public void exceptionOccurred(Exception exception)
		{
			// the query is not finished after the status quo is delivered
		}
	}
	
	/**
	 *  Async filter for checking queries in one go.
	 */
	protected class QueryFilter<T> implements IAsyncFilter<T>
	{
		/** The query. */
		protected ServiceQuery<T> query;
		
		/**
		 *  Create filter. 
		 *  @param query The query.
		 */
		public QueryFilter(ServiceQuery<T> query)
		{
			this.query = query;
		}
		
		/**
		 *  Filter.
		 */
		@SuppressWarnings("unchecked")
		public IFuture<Boolean> filter(T obj)
		{
			Future<Boolean> fret = new Future<Boolean>();
			
			IService ser = (IService) obj;
			if (!(checkSearchScope(query.getProvider(), ser, query.getScope(), false) && checkPublicationScope(query.getProvider(), ser)))
			{
				fret.setResult(Boolean.FALSE);
			}
			else if (query.getFilter() instanceof IAsyncFilter)
			{
				((IAsyncFilter<T>) query.getFilter()).filter(obj).addResultListener(new DelegationResultListener<Boolean>(fret));
			}
			else if (query.getFilter() instanceof IFilter)
			{
				fret.setResult(((IFilter<T>) query.getFilter()).filter(obj));
			}
			else
			{
				fret.setResult(Boolean.TRUE);
			}
			
			return fret;
		}
		
	}
}
