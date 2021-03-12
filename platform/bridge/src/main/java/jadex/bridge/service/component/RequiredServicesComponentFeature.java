package jadex.bridge.service.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.INFPropertyComponentFeature;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.component.impl.IInternalExecutionFeature;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.NFRPropertyInfo;
import jadex.bridge.nonfunctional.AbstractNFProperty;
import jadex.bridge.nonfunctional.INFMixedPropertyProvider;
import jadex.bridge.nonfunctional.INFProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.ServiceIdentifier;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.MultiplicityException;
import jadex.bridge.service.search.ServiceEvent;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceQuery.Multiplicity;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.registry.ISearchQueryManagerService;
import jadex.bridge.service.types.registry.SlidingCuckooFilter;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateEmptyResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateDelegationFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminableFuture;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.commons.future.TerminationCommand;

/**
 *  Feature for provided services.
 */
public class RequiredServicesComponentFeature extends AbstractComponentFeature implements IRequiredServicesFeature, IInternalServiceMonitoringFeature, IInternalRequiredServicesFeature
{
	/** Marker for duplicate declarations of same type. */
	private static final RequiredServiceInfo DUPLICATE_SERVICE_TYPE_MARKER = new RequiredServiceInfo();
	
	//-------- attributes --------
	
	/** The required service infos. */
	protected Map<String, RequiredServiceInfo> requiredserviceinfos;
	
	//-------- monitoring attributes --------
	
	/** The current subscriptions. */
	protected Set<SubscriptionIntermediateFuture<ServiceCallEvent>> subscriptions;
	
	protected ISearchQueryManagerService sqms;
	
	protected List<Tuple2<ServiceQuery<?>, SubscriptionIntermediateDelegationFuture<?>>> delayedremotequeries;
	
	//-------- constructors --------
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public RequiredServicesComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}

	/**
	 *  Init the required services
	 */
	public IFuture<Void> init()
	{
		ServiceQuery<ISearchQueryManagerService> query = new ServiceQuery<>(ISearchQueryManagerService.class);
		UnresolvedServiceInvocationHandler h = new UnresolvedServiceInvocationHandler(component, query);
		//sqms = (ISearchQueryManagerService) ProxyFactory.newProxyInstance(getComponent().getClassLoader(), new Class[]{IService.class, ISearchQueryManagerService.class}, h);
		
		sqms = searchLocalService(new ServiceQuery<>(query).setMultiplicity(0));
		if(sqms == null)
		{
			delayedremotequeries = new ArrayList<>();
			
			ISubscriptionIntermediateFuture<ISearchQueryManagerService> sqmsfut = addQuery(query);
			sqmsfut.addResultListener(new IntermediateEmptyResultListener<ISearchQueryManagerService>()
			{
				public void intermediateResultAvailable(ISearchQueryManagerService result)
				{
					//System.out.println("ISearchQueryManagerService "+result);
					if(sqms == null)
					{
						sqms = result;
						sqmsfut.terminate();
						for (Tuple2<ServiceQuery<?>, SubscriptionIntermediateDelegationFuture<?>> sqi : delayedremotequeries)
						{
							ISubscriptionIntermediateFuture<?> dfut = addQuery(sqi.getFirstEntity());
							FutureFunctionality.connectDelegationFuture(sqi.getSecondEntity(), dfut);
						}
						delayedremotequeries = null;
					}
				}
			});
		}
		/*else
		{
			System.out.println("directly found ISearchQueryManagerService");
		}*/
		
		IModelInfo model = getComponent().getModel();
		ClassLoader cl = getComponent().getClassLoader();
		String config = getComponent().getConfiguration();
		
		// Required services. (Todo: prefix for capabilities)
		RequiredServiceInfo[] ms = model.getServices();
		
		Map<String, RequiredServiceInfo> sermap = new LinkedHashMap<String, RequiredServiceInfo>();
		for(int i=0; i<ms.length; i++)
		{
			ms[i] = new RequiredServiceInfo(/*getServicePrefix()+*/ms[i].getName(), ms[i].getType().getType(cl, model.getAllImports()), ms[i].getMin(), ms[i].getMax(), 
				ms[i].getDefaultBinding(), ms[i].getNFRProperties(), ms[i].getTags());
			sermap.put(ms[i].getName(), ms[i]);
		}

		if(config!=null && model.getConfiguration(config)!=null)
		{
			ConfigurationInfo cinfo = model.getConfiguration(config);
			RequiredServiceInfo[] cs = cinfo.getServices();
			for(int i=0; i<cs.length; i++)
			{
				RequiredServiceInfo rsi = sermap.get(/*getServicePrefix()+*/cs[i].getName());
				RequiredServiceInfo newrsi = new RequiredServiceInfo(rsi.getName(), rsi.getType().getType(cl, model.getAllImports()), ms[i].getMin(), ms[i].getMax(), 
					new RequiredServiceBinding(cs[i].getDefaultBinding()), ms[i].getNFRProperties(), ms[i].getTags());
				sermap.put(rsi.getName(), newrsi);
			}
		}
		
		// Todo: Bindings from outside
		RequiredServiceBinding[] bindings = cinfo.getRequiredServiceBindings();
		if(bindings!=null)
		{
			for(int i=0; i<bindings.length; i++)
			{
				RequiredServiceInfo rsi = sermap.get(bindings[i].getName());
				RequiredServiceInfo newrsi = new RequiredServiceInfo(rsi.getName(), rsi.getType().getType(cl, model.getAllImports()), ms[i].getMin(), ms[i].getMax(), 
					new RequiredServiceBinding(bindings[i]), ms[i].getNFRProperties(), ms[i].getTags());
				sermap.put(rsi.getName(), newrsi);
			}
		}
		
		RequiredServiceInfo[] rservices = sermap.values().toArray(new RequiredServiceInfo[sermap.size()]);
		addRequiredServiceInfos(rservices);
		
		// Todo: Create place holder required service properties 	
//		for(RequiredServiceInfo rsi: rservices)
//		{
//			List<NFRPropertyInfo> nfprops = rsi.getNFRProperties();
//			if(nfprops!=null)
//			{
//				INFMixedPropertyProvider nfpp = getRequiredServicePropertyProvider(null); // null for unbound
//				
//				for(NFRPropertyInfo nfprop: nfprops)
//				{
//					MethodInfo mi = nfprop.getMethodInfo();
//					Class<?> clazz = nfprop.getClazz().getType(cl, model.getAllImports());
//					INFProperty<?, ?> nfp = AbstractNFProperty.createProperty(clazz, getComponent(), null, nfprop.getMethodInfo());
//					if(mi==null)
//					{
//						nfpp.addNFProperty(nfp);
//					}
//					else
//					{
//						nfpp.addMethodNFProperty(mi, nfp);
//					}
//				}
//			}
//		}
					
		return IFuture.DONE;
	}
	
	/**
	 *  Check if the feature potentially executed user code in body.
	 *  Allows blocking operations in user bodies by using separate steps for each feature.
	 *  Non-user-body-features are directly executed for speed.
	 *  If unsure just return true. ;-)
	 */
	public boolean hasUserBody()
	{
		return false;
	}
	
	/**
	 *  Called when the feature is shutdowned.
	 */
	public IFuture<Void> shutdown()
	{
		// Remove the persistent queries
		ServiceRegistry.getRegistry(component).removeQueries(getComponent().getId());
		return IFuture.DONE;
	}
	
	/**
	 *  Add required services for a given prefix.
	 *  @param prefix The name prefix to use.
	 *  @param required services The required services to set.
	 */
	protected void addRequiredServiceInfos(RequiredServiceInfo[] requiredservices)
	{
//		if(shutdowned)
//			throw new ComponentTerminatedException(id);

		if(requiredservices!=null && requiredservices.length>0)
		{
			if(this.requiredserviceinfos==null)
				this.requiredserviceinfos = new HashMap<String, RequiredServiceInfo>();
			for(int i=0; i<requiredservices.length; i++)
			{
				this.requiredserviceinfos.put(requiredservices[i].getName(), requiredservices[i]);
				if(requiredservices[i].getType()!=null)
				{
					if(requiredserviceinfos.containsKey(requiredservices[i].getType().getTypeName()))
					{
						this.requiredserviceinfos.put(requiredservices[i].getType().getTypeName(), DUPLICATE_SERVICE_TYPE_MARKER);
					}
					else
					{
						this.requiredserviceinfos.put(requiredservices[i].getType().getTypeName(), requiredservices[i]);
					}
				}
			}
		}
	}
	
	//-------- accessors for declared services --------
	
	/**
	 *  Resolve a declared required service of a given name.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public <T> IFuture<T> getService(String name)
	{
		return resolveService(getServiceQuery(getServiceInfo(name)), getServiceInfo(name));
	}
	
	/**
	 *  Resolve a required service of a given type.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param type The service type.
	 *  @return The service.
	 */
	public <T> IFuture<T> getService(Class<T> type)
	{
		RequiredServiceInfo info = getServiceInfo(type);
		if(info==null)
		{
			// Convenience case: switch to search when type not declared
			return searchService(new ServiceQuery<>(type));
		}
		else
		{
			return resolveService(getServiceQuery(info), info);
			
		}
	}
	
	/**
	 *  Resolve a required services of a given name.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param name The services name.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> getServices(String name)
	{
		return resolveServices(getServiceQuery(getServiceInfo(name)), getServiceInfo(name));
	}
	
	/**
	 *  Resolve a required services of a given type.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param type The services type.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> getServices(Class<T> type)
	{
		RequiredServiceInfo info = getServiceInfo(type);
		if(info==null)
		{
			// Convenience case: switch to search when type not declared
			return searchServices(new ServiceQuery<>(type));
		}
		else
		{
			return resolveServices(getServiceQuery(info), info);
			
		}
	}
	
	/**
	 *  Resolve a declared required service of a given name.
	 *  Synchronous method only for locally available services.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public <T> T getLocalService(String name)
	{
		return resolveLocalService(getServiceQuery(getServiceInfo(name)), getServiceInfo(name));
	}
	
	/**
	 *  Resolve a required service of a given type.
	 *  Synchronous method only for locally available services.
	 *  @param type The service type.
	 *  @return The service.
	 */
	public <T> T getLocalService(Class<T> type)
	{
		RequiredServiceInfo info = getServiceInfo(type);
		if(info==null)
		{
			// Convenience case: switch to search when type not declared
			return searchLocalService(new ServiceQuery<>(type));
		}
		else
		{
			return resolveLocalService(getServiceQuery(info), info);
			
		}
	}
	
	/**
	 *  Resolve a required service of a given type.
	 *  Synchronous method only for locally available services.
	 *  @param type The service type.
	 *  @return The service.
	 */
	public <T> T getLocalService0(Class<T> type)
	{
		RequiredServiceInfo info = getServiceInfo(type);
		if(info==null)
		{
			// Convenience case: switch to search when type not declared
			return searchLocalService(new ServiceQuery<>(type).setMultiplicity(Multiplicity.ZERO_ONE));
		}
		else
		{
			ServiceQuery<T> sq = (ServiceQuery<T>)getServiceQuery(info).setMultiplicity(Multiplicity.ZERO_ONE);
			return resolveLocalService(sq, info);
			
		}
	}
	
	/**
	 *  Resolve a required services of a given name.
	 *  Synchronous method only for locally available services.
	 *  @param name The services name.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> Collection<T> getLocalServices(String name)
	{
		return resolveLocalServices(getServiceQuery(getServiceInfo(name)), getServiceInfo(name)); 	
	}
	
	/**
	 *  Resolve a required services of a given type.
	 *  Synchronous method only for locally available services.
	 *  @param type The services type.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> Collection<T> getLocalServices(Class<T> type)
	{
		RequiredServiceInfo info = getServiceInfo(type);
		if(info==null)
		{
			// Convenience case: switch to search when type not declared
			return searchLocalServices(new ServiceQuery<>(type));
		}
		else
		{
			return resolveLocalServices(getServiceQuery(info), info);
			
		}
	}
	
	//-------- methods for searching --------
	
	/**
	 *  Search for matching services and provide first result.
	 *  @param query The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> IFuture<T> searchService(ServiceQuery<T> query)
	{
		return resolveService(query, ServiceQuery.createServiceInfo(query));
	}
	
	/**
	 *  Search for matching services and provide first result.
	 *  Synchronous method only for locally available services.
	 *  @param query The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> T searchLocalService(ServiceQuery<T> query)
	{
		return resolveLocalService(query, ServiceQuery.createServiceInfo(query));
	}
	
	/**
	 *  Search for all matching services.
	 *  @param query The search query.
	 *  @return Future providing the corresponding services or ServiceNotFoundException when not found.
	 */
	public <T>  ITerminableIntermediateFuture<T> searchServices(ServiceQuery<T> query)
	{
		return resolveServices(query, ServiceQuery.createServiceInfo(query));
	}
	
	/**
	 *  Search for all matching services.
	 *  Synchronous method only for locally available services.
	 *  @param query The search query.
	 *  @return Future providing the corresponding services or ServiceNotFoundException when not found.
	 */
	public <T> Collection<T> searchLocalServices(ServiceQuery<T> query)
	{
		return resolveLocalServices(query, ServiceQuery.createServiceInfo(query));
	}
	
	/**
	 *  Performs a sustained search for a service. Attempts to find a service
	 *  for a maximum duration until timeout occurs.
	 *  
	 *  @param query The search query.
	 *  @param timeout Maximum time period to search, -1 for no wait.
	 *  @return Service matching the query, exception if service is not found.
	 */
	public <T> IFuture<T> searchService(ServiceQuery<T> query, long timeout)
	{
		Future<T> ret = new Future<T>();
		timeout = timeout != 0 ? timeout : Starter.getDefaultTimeout(component.getId());
		
		ISubscriptionIntermediateFuture<T> queryfut = addQuery(query);
		
		queryfut.addResultListener(new IntermediateEmptyResultListener<T>()
		{
			public void exceptionOccurred(Exception exception)
			{
				ret.setExceptionIfUndone(exception);
			}

			public void intermediateResultAvailable(T result)
			{
				ret.setResultIfUndone(result);
				queryfut.terminate();
			}
		});
		
		long to = timeout;
		//isRemote(query)
		
		if(to>0)
		{
			component.waitForDelay(timeout, true).then(done -> 
			{
				Multiplicity m = query.getMultiplicity();
				if(m.getFrom()>0)
				{
					queryfut.terminate(new ServiceNotFoundException("Service " + query + " not found in search period " + to));
				}
				else
				{
					queryfut.terminate();
				}
			});
		}
		
		return ret;
	}
	
	//-------- query methods --------

	/**
	 *  Add a query for a declared required service.
	 *  Continuously searches for matching services.
	 *  @param name The name of the required service declaration.
	 *  @return Future providing the corresponding services as intermediate results.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(ServiceQuery<T> query, long timeout)
	{
		SubscriptionIntermediateDelegationFuture<T> ret = new SubscriptionIntermediateDelegationFuture<>();
		
		timeout = timeout != 0 ? timeout : Starter.getDefaultTimeout(component.getId());
		
		ISubscriptionIntermediateFuture<T> queryfut = addQuery(query);
		
		final int[] resultcnt = new int[1];
		queryfut.addResultListener(new IIntermediateResultListener<T>()
		{
			public void resultAvailable(Collection<T> result)
			{
				for(T r: result)
					intermediateResultAvailable(r);
				finished();
			}

			public void exceptionOccurred(Exception exception)
			{
				ret.setExceptionIfUndone(exception);
			}

			public void intermediateResultAvailable(T result)
			{
				resultcnt[0]++;
				ret.addIntermediateResultIfUndone(result);
			}

			public void finished()
			{
				ret.setFinishedIfUndone();
			}
			
			public void maxResultCountAvailable(int max) 
			{
				ret.setMaxResultCount(max);
			}
		});
		
		long to = timeout;
		//isRemote(query)
		
		if(to>0)
		{
			component.waitForDelay(timeout, true).then(done -> 
			{
				Exception e;
				Multiplicity m = query.getMultiplicity();
				if(m.getFrom()>0 && resultcnt[0]<m.getFrom()
					|| m.getTo()>0 && resultcnt[0]>m.getTo())
				{
					e = new MultiplicityException("["+m.getFrom()+"-"+m.getTo()+"]"+", resultcnt="+resultcnt[0]);
				}
				else
				{
					e = new TimeoutException();
					//new ServiceNotFoundException("Service " + query + " not found in search period " + to)
				}
				queryfut.terminate(e);
			});
		}
		
		return ret;
	}
	
	/**
	 *  Add a query for a declared required service.
	 *  Continuously searches for matching services.
	 *  @param name The name of the required service declaration.
	 *  @return Future providing the corresponding services as intermediate results.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(String name)
	{
		return resolveQuery(getServiceQuery(getServiceInfo(name)), getServiceInfo(name));
	}

	/**
	 *  Add a query for a declared required service.
	 *  Continuously searches for matching services.
	 *  @param type The type of the required service declaration.
	 *  @return Future providing the corresponding services as intermediate results.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(Class<T> type)
	{
		return resolveQuery(getServiceQuery(getServiceInfo(type)), getServiceInfo(type));
	}

	/**
	 *  Add a service query.
	 *  Continuously searches for matching services.
	 *  @param query The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(ServiceQuery<T> query)
	{
		return resolveQuery(query, ServiceQuery.createServiceInfo(query));
	}
	
	//-------- event interface --------
	
	/**
	 *  Get the required services.
	 *  @return The required services.
	 */
	public RequiredServiceInfo[] getServiceInfos()
	{
//		if(shutdowned)
//			throw new ComponentTerminatedException(id);
		
		// Convert to set to remove duplicate entries (name+type) and exclude marker.
		Set<RequiredServiceInfo>	ret	= new LinkedHashSet<>();
		if(requiredserviceinfos!=null)
		{
			for(RequiredServiceInfo info: requiredserviceinfos.values())
			{
				if(!DUPLICATE_SERVICE_TYPE_MARKER.equals(info))
				{
					ret.add(info);
				}
			}
		}
		return ret.toArray(new RequiredServiceInfo[ret.size()]);
	}
	
	/**
	 *  Listen to service call events (call, result and commands).
	 */
	// Todo: only match specific calls?
	// Todo: Commands
	public ISubscriptionIntermediateFuture<ServiceCallEvent> getServiceEvents()
	{
		if(subscriptions==null)
		{
			subscriptions = new LinkedHashSet<SubscriptionIntermediateFuture<ServiceCallEvent>>();
		}
		@SuppressWarnings("unchecked")
		final SubscriptionIntermediateFuture<ServiceCallEvent> ret = (SubscriptionIntermediateFuture<ServiceCallEvent>)
			SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, getInternalAccess());
		ret.setTerminationCommand(new TerminationCommand()
		{
			@Override
			public void terminated(Exception reason)
			{
				subscriptions.remove(ret);
				if(subscriptions.isEmpty())
				{
					subscriptions = null;
				}
			}
		});
		subscriptions.add(ret);
		return ret;
	}
	
	/**
	 *  Post a service call event.
	 */
	public void postServiceEvent(ServiceCallEvent event)
	{
		if(subscriptions!=null)
		{
			for(SubscriptionIntermediateFuture<ServiceCallEvent> sub: subscriptions)
			{
				sub.addIntermediateResult(event);
			}
		} 
	}

	/**
	 *  Check if there is someone monitoring.
	 *  To Avoid posting when nobody is listening.
	 */
	public boolean isMonitoring()
	{
		return subscriptions!=null;
	}
	
	//-------- convenience methods --------
	
	/**
	 *  Get a service raw (i.e. w/o required proxy).
	 *  @return null when not found.
	 */
	public <T> T getRawService(Class<T> type)
	{
		try
		{
			ServiceQuery<T> query = new ServiceQuery<>(type).setMultiplicity(Multiplicity.ZERO_ONE);
			query.setRequiredProxyType(ServiceQuery.PROXYTYPE_RAW);
			return resolveLocalService(query, ServiceQuery.createServiceInfo(query));
		}
		catch(ServiceNotFoundException snfe)
		{
			return null;
		}
	}

	/**
	 *  Get a service raw (i.e. w/o required proxy).
	 */
	public <T> Collection<T> getRawServices(Class<T> type)
	{
		ServiceQuery<T> query = new ServiceQuery<>(type);
		query.setRequiredProxyType(ServiceQuery.PROXYTYPE_RAW);
		return resolveLocalServices(query, ServiceQuery.createServiceInfo(query));
	}

	
	//-------- impl/raw methods --------
	
	/**
	 * 
	 * @param result
	 * @param info
	 * @return
	 */
	protected Object processResult(Object result, RequiredServiceInfo info)
	{
		if(result instanceof ServiceEvent)
			return processServiceEvent((ServiceEvent)result, info);
		else if(result instanceof IServiceIdentifier)
			return getServiceProxy((IServiceIdentifier)result, info);
		else if(result instanceof IService)
			return addRequiredServiceProxy((IService)result, info);
		else
			return result;
	}
	
	/**
	 *  Search for matching services and provide first result.
	 *  @param query The search query.
	 *  @param info Used for required service proxy configuration -> null for no proxy.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> ITerminableFuture<T> resolveService(ServiceQuery<T> query, RequiredServiceInfo info)
	{
		enhanceQuery(query, false);
		Future<T> ret = null;
		
		// Try to find locally
		IServiceIdentifier sid = ServiceRegistry.getRegistry(getInternalAccess()).searchService(query);
		if(sid!=null)
		{
			ret = new TerminableFuture<>();
			@SuppressWarnings("unchecked")
			T t = (T)getServiceProxy(sid, info);
			ret.setResult(t);
		}
		
		// If not found -> try to find remotely
		else if(isRemote(query) && sqms != null)
		{
//			ISearchQueryManagerService sqms = searchLocalService(new ServiceQuery<>(ISearchQueryManagerService.class).setMultiplicity(Multiplicity.ZERO_ONE));
//			if(sqms!=null)
//			{
			
			@SuppressWarnings("rawtypes")
			ITerminableFuture fut = sqms.searchService(query);
			@SuppressWarnings("unchecked")
			ITerminableFuture<T> castedfut = (ITerminableFuture<T>) fut;
			ret = FutureFunctionality.getDelegationFuture(castedfut, new FutureFunctionality(getComponent().getLogger())
			{
				@Override
				public Object handleResult(Object result) throws Exception
				{
					// todo: remove after superpeer fix
					result = processResult(result, info);
					if(result==null)
					{
						if(query.getMultiplicity().getFrom()!=0)
						{
							throw new ServiceNotFoundException(query.toString());
						}
					}
					return result;
					
					//return processResult(result, info);
				}
			});
			((IInternalExecutionFeature)component.getFeature(IExecutionFeature.class)).addSimulationBlocker(ret);
			
//			}
		}
		
		// Not found locally and query not remote or no remote search manager available
		if(ret==null)
		{
			ret = new TerminableFuture<>();
			if(query.getMultiplicity().getFrom()==0)
			{
				ret.setResult(null);
			}
			else
			{
				ret.setException(new ServiceNotFoundException(query.toString()));
			}
		}
		
		@SuppressWarnings("unchecked")
		ITerminableFuture<T> iret = (ITerminableFuture<T>)ret;
		return iret;
	}
	
	/**
	 *  Search for matching services and provide first result.
	 *  Synchronous method only for locally available services.
	 *  @param query The search query.
	 *  @param info Used for required service proxy configuration -> null for no proxy.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> T resolveLocalService(ServiceQuery<T> query, RequiredServiceInfo info)
	{
		enhanceQuery(query, false);
		
		IServiceIdentifier sid = ServiceRegistry.getRegistry(getInternalAccess()).searchService(query);
		
		if(sid==null && query.getMultiplicity().getFrom()>0)
			throw new ServiceNotFoundException(query.toString());
				
		// Fetches service and wraps result in proxy, if required. 
		@SuppressWarnings("unchecked")
		T ret = sid!=null ? (T)getServiceProxy(sid, info) : null;
		return ret;
	}
	
	/**
	 *  Search for all matching services.
	 *  @param query The search query.
	 *  @param info Used for required service proxy configuration -> null for no proxy.
	 *  @return Future providing the corresponding services or ServiceNotFoundException when not found.
	 */
	public <T>  ITerminableIntermediateFuture<T> resolveServices(ServiceQuery<T> query, RequiredServiceInfo info)
	{
		ITerminableIntermediateFuture<T> ret;
		
//		if(query.getServiceType().toString().indexOf("ITransportInfoService")!=-1)
//			System.out.println("here");
		
		// Check if remote
//		ISearchQueryManagerService sqms = isRemote(query) ? searchLocalService(new ServiceQuery<>(ISearchQueryManagerService.class).setMultiplicity(Multiplicity.ZERO_ONE)) : null;
//		if(isRemote(query) && sqms==null)
//		{
//			getComponent().getLogger().warning("No ISearchQueryManagerService found for remote search: "+query);
////			return new TerminableIntermediateFuture<>(new IllegalStateException("No ISearchQueryManagerService found for remote search: "+query));
//		}
		
		//final int min = query.getMultiplicity()!=null? query.getMultiplicity().getFrom(): -1;
		final int max = query.getMultiplicity()!=null? query.getMultiplicity().getTo(): -1;
		final int[] resultcnt = new int[1];
		
		// Local only -> create future, fill results, and set to finished.
		if(!isRemote(query) || sqms == null)
		{
			TerminableIntermediateFuture<T>	fut	= new TerminableIntermediateFuture<>();
			ret	= fut;
			
			// Find local matches (also enhances query, hack!?)
			Collection<T> locals = resolveLocalServices(query, info);
			for(T result: locals)
			{
				if(max<0 || ++resultcnt[0]<=max)
				{
					fut.addIntermediateResult(result);
					// if next result is not allowed any more
					if(max>0 && resultcnt[0]+1>max)
					{
						// Finish the user side and terminate the source side
						fut.setFinishedIfUndone();
						Exception reason = new MultiplicityException("Max number of values received: "+max);
						fut.terminate(reason);
					}
				}
				else
				{
					break;
				}
			}
			fut.setFinishedIfUndone();
		}

		// Find remote matches, if needed
		else
		{
			enhanceQuery(query, true);
			SlidingCuckooFilter scf = new SlidingCuckooFilter();
			
			// Search remotely and connect to delegation future.
			ITerminableIntermediateFuture<IServiceIdentifier> remotes = sqms.searchServices(query);

			final IntermediateFuture<IServiceIdentifier>[] futs = new IntermediateFuture[1];
			
			// Combined delegation future for local and remote results.
			futs[0] = (IntermediateFuture<IServiceIdentifier>)FutureFunctionality
				.getDelegationFuture(ITerminableIntermediateFuture.class, new ComponentFutureFunctionality(getInternalAccess())
			{
				@Override
				public Object handleIntermediateResult(Object result) throws Exception
				{
					// Drop result when already in cuckoo filter
					if(scf.contains(result.toString()))
					{
						return DROP_INTERMEDIATE_RESULT;
					}
					else
					{
						if(max<0 || ++resultcnt[0]<=max)
						{
							scf.insert(result.toString());
							return processResult(result, info);
						}
						else
						{
							//System.out.println("fut drop: "+hashCode());
							return DROP_INTERMEDIATE_RESULT;
						}
					}
				}
				
				@Override
				public void handleAfterIntermediateResult(Object result) throws Exception
				{
					if(DROP_INTERMEDIATE_RESULT.equals(result))
						return;
					
					// if next result is not allowed any more
					if(max>0 && resultcnt[0]+1>max)
					{
						// Finish the user side and terminate the source side
						futs[0].setFinishedIfUndone();
						Exception reason = new MultiplicityException("Max number of values received: "+max);
						//System.out.println("fut terminate: "+hashCode());
						remotes.terminate(reason);
					}
				}
				
				@Override
				public void handleTerminated(Exception reason)
				{
					//System.out.println("fut terminated: "+hashCode());
					super.handleTerminated(reason);
				}
				
				@Override
				public void handleFinished(Collection<Object> results) throws Exception
				{
					//System.out.println("fut fin: "+hashCode());
					super.handleFinished(results);
				}
			});
			
			// Manually add local results to delegation future
			IServiceRegistry registry = ServiceRegistry.getRegistry(getInternalAccess());
			Collection<IServiceIdentifier> results =  registry.searchServices(query);
			for(IServiceIdentifier result: results)
			{
				if(max<0 || ++resultcnt[0]<=max)
				{
					futs[0].addIntermediateResult(result);
					// if next result is not allowed any more
					if(max>0 && resultcnt[0]+1>max)
					{
						// Finish the user side and terminate the source side
						futs[0].setFinishedIfUndone();
						Exception reason = new MultiplicityException("Max number of values received: "+max);
						((ITerminableIntermediateFuture<IServiceIdentifier>)futs[0]).terminate(reason);
					}
				}
				else
				{
					break;
				}
			}

//			System.out.println("Search: "+query);
//			remotes.addResultListener(res -> System.out.println("Search finished: "+query));
			FutureFunctionality.connectDelegationFuture(futs[0], remotes); // target, source
			
			@SuppressWarnings("unchecked")
			IIntermediateFuture<T>	casted	= (IIntermediateFuture<T>)futs[0];
			ret	= (ITerminableIntermediateFuture<T>)casted;
//			ret.addResultListener(res -> System.out.println("ret finished: "+query));
		}
		
		// print outs for debugging
		/*ret.addResultListener(new IIntermediateResultListener<T>()
		{
			@Override
			public void intermediateResultAvailable(T result)
			{
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("ex: "+exception+" "+hashCode());
			}
			
			@Override
			public void finished()
			{
				System.out.println("fini: "+hashCode());
			}
			
			@Override
			public void resultAvailable(Collection<T> result)
			{
				System.out.println("resa: "+hashCode());
			}
		});*/
		
		return ret;
	}
	
	/**
	 *  Search for all matching services.
	 *  Synchronous method only for locally available services.
	 *  @param query The search query.
	 *  @param info Used for required service proxy configuration -> null for no proxy.
	 *  @return Future providing the corresponding services or ServiceNotFoundException when not found.
	 */
	public <T> Collection<T> resolveLocalServices(ServiceQuery<T> query, RequiredServiceInfo info)
	{
		enhanceQuery(query, true);
		
		IServiceRegistry registry = ServiceRegistry.getRegistry(getInternalAccess());
		Collection<IServiceIdentifier> results =  registry.searchServices(query);
		
		// Wraps result in proxy, if required. 
		Collection<T> ret = new ArrayList<>();
		for(IServiceIdentifier result: results)
		{
			@SuppressWarnings("unchecked")
			T service = (T)getServiceProxy(result, info);
			ret.add(service);
		}
		
		return ret;
	}
	
	/**
	 *  Query for all matching services.
	 *  @param query The search query.
	 *  @param info Used for required service proxy configuration -> null for no proxy.
	 *  @return Future providing the corresponding services or ServiceNotFoundException when not found.
	 */
	public <T>  ISubscriptionIntermediateFuture<T> resolveQuery(ServiceQuery<T> query, RequiredServiceInfo info)
	{
		enhanceQuery(query, true);
		SlidingCuckooFilter scf = new SlidingCuckooFilter();
		
		//System.out.println("query: "+query);
		
		// Query remote
//		ISearchQueryManagerService sqms = searchLocalService(new ServiceQuery<>(ISearchQueryManagerService.class).setMultiplicity(Multiplicity.ZERO_ONE));
//		if(isRemote(query) && sqms==null)
//		{
//			return new SubscriptionIntermediateFuture<>(new IllegalStateException("No ISearchQueryManagerService found for remote query: "+query));
//		}
		ISubscriptionIntermediateFuture<T> tmpremotes = null;
		if(isRemote(query))
		{
			if(sqms != null)
			{
				tmpremotes = sqms.addQuery(query);
			}
			else
			{
				tmpremotes = new SubscriptionIntermediateDelegationFuture<>();
				Tuple2<ServiceQuery<?>, SubscriptionIntermediateDelegationFuture<?>> sqi = new Tuple2<ServiceQuery<?>, SubscriptionIntermediateDelegationFuture<?>>(query, (SubscriptionIntermediateDelegationFuture<T>) tmpremotes);
				delayedremotequeries.add(sqi);
				return tmpremotes;
			}
		}
		ISubscriptionIntermediateFuture<T> remotes = tmpremotes;
		
		// Query local registry
		IServiceRegistry registry = ServiceRegistry.getRegistry(getInternalAccess());
		ISubscriptionIntermediateFuture<?> localresults = (ISubscriptionIntermediateFuture<?>)registry.addQuery(query);
		
		final int[] resultcnt = new int[1];
		final ISubscriptionIntermediateFuture<T>[] ret = new ISubscriptionIntermediateFuture[1];
		ret[0] = (ISubscriptionIntermediateFuture)FutureFunctionality
			// Component functionality as local registry pushes results on arbitrary thread.
			.getDelegationFuture(localresults, new ComponentFutureFunctionality(getInternalAccess())
		{
			@Override
			public Object handleIntermediateResult(Object result) throws Exception
			{
				// Drop result when already in cuckoo filter
				if(scf.contains(result.toString()))
				{
					return DROP_INTERMEDIATE_RESULT;
				}
				else
				{
					// check multiplicity constraints
					resultcnt[0]++;
					int max = query.getMultiplicity().getTo();
					
					if(max<0 || resultcnt[0]<=max)
					{
						scf.insert(result.toString());
						return processResult(result, info);
					}
					else
					{
						return DROP_INTERMEDIATE_RESULT;
					}
				}
			}
			
			@Override
			public void handleAfterIntermediateResult(Object result) throws Exception
			{
				if(DROP_INTERMEDIATE_RESULT.equals(result))
					return;
				
				int max = query.getMultiplicity().getTo();
				// if next result is not allowed any more
				if(max>0 && resultcnt[0]+1>max)
				{
					((IntermediateFuture)ret[0]).setFinishedIfUndone();
					Exception reason = new MultiplicityException("Max number of values received: "+max);
					if(remotes!=null)
						remotes.terminate(reason);
					localresults.terminate(reason);
				}
			}
			
			@Override
			public void handleTerminated(Exception reason)
			{
				//System.out.println("terminated called: "+reason);
				
				// TODO: multi delegation future with multiple sources but one target?
				if(remotes!=null)
					remotes.terminate(reason);
				
				super.handleTerminated(reason);
			}
		});
		
		// print outs for debugging
		/*ret.addResultListener(new IIntermediateResultListener<T>()
		{
			@Override
			public void intermediateResultAvailable(T result)
			{
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("ex: "+exception+" "+hashCode());
			}
			
			@Override
			public void finished()
			{
				System.out.println("fini: "+hashCode());
			}
			
			@Override
			public void resultAvailable(Collection<T> result)
			{
			}
		});*/
		
		// Add remote results to future (functionality handles wrapping)
		if(remotes!=null)
		{
			remotes.next(
				result->
				{
					@SuppressWarnings("unchecked")
					IntermediateFuture<T> fut = (IntermediateFuture<T>)ret[0];
					fut.addIntermediateResultIfUndone(result);
				})
			.catchEx(exception -> {}); // Ignore exception (printed when no listener supplied)
		}
		
		return ret[0];
	}
	
	//-------- helper methods --------
	
	/**
	 * When searching for declared service -> map required service declaration to service query.
	 */
	protected <T> ServiceQuery<T> getServiceQuery(RequiredServiceInfo info)
	{
		return ServiceQuery.getServiceQuery(getComponent().getInternalAccess(), info);
	}
	
	
	
	/**
	 *  Get the required service info for a name.
	 *  @param name The required service name.
	 */
	// Hack!!! used by multi invoker?
	public RequiredServiceInfo getServiceInfo(String name)
	{
		RequiredServiceInfo info = requiredserviceinfos==null ? null : requiredserviceinfos.get(name);
		if(info==null)
			throw new IllegalArgumentException("No such required service: "+name);
		return info;
	}
	
	/**
	 *  Get the required service info for a type.
	 *  @param type The required service type.
	 */
	protected RequiredServiceInfo getServiceInfo(Class<?> type)
	{
		RequiredServiceInfo info = requiredserviceinfos==null ? null : requiredserviceinfos.get(SReflect.getClassName(type));
		if(info==DUPLICATE_SERVICE_TYPE_MARKER)
			throw new IllegalArgumentException("Multiple required service declarations found for type: "+type);
		return info;
	}
	
	/**
	 *  Create the user-facing object from the received search or query result.
	 *  Result may be service object, service identifier (local or remote), or event.
	 *  User object is either event or service (with or without required proxy).
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ServiceEvent processServiceEvent(ServiceEvent event, RequiredServiceInfo info)
	{
		if(event.getService() instanceof IService)
		{
			IService service = addRequiredServiceProxy((IService)event.getService(), info);
			event.setService(service);
		}
		else if(event.getService() instanceof IServiceIdentifier
			&& event.getType()!=ServiceEvent.SERVICE_REMOVED)
		{
			IService service = getServiceProxy((IServiceIdentifier)event.getService(), info);
			// can null when service is not available any more
			if(service!=null)
				event.setService(service);
		}
		
		return event;
	}
	
	/**
	 *  Create the user-facing object from the received search or query result.
	 *  Result may be service object, service identifier (local or remote), or event.
	 *  User object is either event or service (with or without required proxy).
	 */
	@SuppressWarnings({"rawtypes", "unchecked" })
	public IService getServiceProxy(IServiceIdentifier sid, RequiredServiceInfo info)
	{
		IService ret = null;
		
		// If service identifier -> find/create service object or proxy
			
		// Local component -> fetch local service object.
		if(sid.getProviderId().getRoot().equals(getComponent().getId().getRoot()))
		{
			ret = ServiceRegistry.getRegistry(getInternalAccess()).getLocalService(sid); 			
		}
		
		// Remote component -> create remote proxy
		else
		{
			ret = RemoteMethodInvocationHandler.createRemoteServiceProxy(getInternalAccess(), sid);
		}
		
		// else service event -> just return event, as desired by user (specified in query return type)
		
		if(ret!=null)
			ret = addRequiredServiceProxy(ret, info);
		
		return ret;
	}

	/**
	 * 
	 * @param service
	 * @param info
	 */
	protected IService addRequiredServiceProxy(IService service, RequiredServiceInfo info)
	{
		IService ret = service;
		
		// Add required service proxy if specified.
		if(info!=null)
		{
			ret = BasicServiceInvocationHandler.createRequiredServiceProxy(getInternalAccess(), 
				(IService)ret, null, info, info.getDefaultBinding(), Starter.isRealtimeTimeout(getComponent().getId()));
			
			// Check if no property provider has been created before and then create and init properties
			if(!getComponent().getFeature(INFPropertyComponentFeature.class).hasRequiredServicePropertyProvider(ret.getServiceId()))
			{
				INFMixedPropertyProvider nfpp = getComponent().getFeature(INFPropertyComponentFeature.class)
					.getRequiredServicePropertyProvider(((IService)ret).getServiceId());
				
				List<NFRPropertyInfo> nfprops = info.getNFRProperties();
				if(nfprops!=null && nfprops.size()>0)
				{
					for(NFRPropertyInfo nfprop: nfprops)
					{
						MethodInfo mi = nfprop.getMethodInfo();
						Class<?> clazz = nfprop.getClazz().getType(getComponent().getClassLoader(), getComponent().getModel().getAllImports());
						INFProperty<?, ?> nfp = AbstractNFProperty.createProperty(clazz, getInternalAccess(), (IService)ret, nfprop.getMethodInfo(), nfprop.getParameters());
						if(mi==null)
						{
							nfpp.addNFProperty(nfp);
						}
						else
						{
							nfpp.addMethodNFProperty(mi, nfp);
						}
					}
				}
			}
		}
		
		return ret;
	}
	

	/**
	 *  Enhance a query before processing.
	 *  Does some necessary preprocessing and needs to be called at least once before processing the query.
	 *  @param query The query to be enhanced.
	 */
	protected <T> void enhanceQuery(ServiceQuery<T> query, boolean multi)
	{
//		if(shutdowned)
//			return new Future<T>(new ComponentTerminatedException(id));

		// Set owner if not set
		if(query.getOwner()==null)
			query.setOwner(getComponent().getId());
		
		// Set scope if not set
		if(ServiceScope.DEFAULT.equals(query.getScope()))
		{
			// Default to application if service type not set or not system service
			query.setScope(query.getServiceType()!=null && ServiceIdentifier.isSystemService(query.getServiceType().getType(getComponent().getClassLoader()))
				? ServiceScope.PLATFORM : ServiceScope.APPLICATION);
		}
		
		if(query.getMultiplicity()==null)
		{
			// Fix multiple flag according to single/multi method 
			query.setMultiplicity(multi ? Multiplicity.ZERO_MANY : Multiplicity.ONE);
		}
		
		//if(query.getMultiplicity()!=null && query.getMultiplicity().getTo()==0)
		//	throw new MultiplicityException();
		
		// Network names not set by user?
		if(Arrays.equals(query.getNetworkNames(), ServiceQuery.NETWORKS_NOT_SET))
		{
			// Local or unrestricted?
			if(!isRemote(query) || Boolean.TRUE.equals(query.isUnrestricted())
				|| query.getServiceType()!=null && ServiceIdentifier.isUnrestricted(getInternalAccess(), query.getServiceType().getType(getComponent().getClassLoader()))) 
			{
				// Unrestricted -> Don't check networks.
				query.setNetworkNames((String[])null);
			}
			else
			{
				// Not unrestricted -> only find services from my local networks
				@SuppressWarnings("unchecked")
				Set<String> nnames = (Set<String>)Starter.getPlatformValue(getComponent().getId(), Starter.DATA_NETWORKNAMESCACHE);
				query.setNetworkNames(nnames!=null? nnames.toArray(new String[nnames.size()]): SUtil.EMPTY_STRING_ARRAY);
			}
		}
	}
	
	/**
	 *  Check if a query is potentially remote.
	 *  @return True, if scope is set to a remote scope (e.g. global or network).
	 */
	public boolean isRemote(ServiceQuery<?> query)
	{
		return query.getSearchStart()!=null && query.getSearchStart().getRoot()!=getComponent().getId().getRoot()
			|| !query.getScope().isLocal();
	}
	
	/**
	 *  Get a service query for a required service info (as defined in the agent under that name).
	 *  @param name The name.
	 *  @return The service query.
	 */
	public ServiceQuery<?> getServiceQuery(String name)
	{
		return getServiceQuery(getServiceInfo(name));
	}
}
