package jadex.bridge.service.component;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.bridge.SFuture;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.component.multiinvoke.MultiServiceInvocationHandler;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.commons.IAsyncFilter;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.commons.future.TerminationCommand;

/**
 *  Feature for provided services.
 */
// Todo: synchronous or asynchronous (for search)?
public class RequiredServicesComponentFeature	extends AbstractComponentFeature implements IRequiredServicesFeature, IInternalServiceMonitoringFeature
{
	//-------- attributes --------
	
	/** The required service infos. */
	protected Map<String, RequiredServiceInfo> requiredserviceinfos;
	
	//-------- monitoring attributes --------
	
	/** The current subscriptions. */
	protected Set<SubscriptionIntermediateFuture<ServiceCallEvent>>	subscriptions;
	
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
		IModelInfo	model	= getComponent().getModel();
		ClassLoader	cl	= getComponent().getClassLoader();
		String	config	= getComponent().getConfiguration();
		
		// Required services. (Todo: prefix for capabilities)
		RequiredServiceInfo[] ms = model.getRequiredServices();
		
		Map<String, RequiredServiceInfo>	sermap = new LinkedHashMap<String, RequiredServiceInfo>();
		for(int i=0; i<ms.length; i++)
		{
			ms[i]	= new RequiredServiceInfo(/*getServicePrefix()+*/ms[i].getName(), ms[i].getType().getType(cl, model.getAllImports()), ms[i].isMultiple(), 
				ms[i].getDefaultBinding(), ms[i].getNFRProperties(), ms[i].getTags());
			sermap.put(ms[i].getName(), ms[i]);
		}

		if(config!=null && model.getConfiguration(config)!=null)
		{
			ConfigurationInfo cinfo = model.getConfiguration(config);
			RequiredServiceInfo[] cs = cinfo.getRequiredServices();
			for(int i=0; i<cs.length; i++)
			{
				RequiredServiceInfo rsi = (RequiredServiceInfo)sermap.get(/*getServicePrefix()+*/cs[i].getName());
				RequiredServiceInfo newrsi = new RequiredServiceInfo(rsi.getName(), rsi.getType().getType(cl, model.getAllImports()), rsi.isMultiple(), 
					new RequiredServiceBinding(cs[i].getDefaultBinding()), ms[i].getNFRProperties(), ms[i].getTags());
				sermap.put(rsi.getName(), newrsi);
			}
		}
		
		// Todo: Bindings from outside
		RequiredServiceBinding[]	bindings	= cinfo.getRequiredServiceBindings();
		if(bindings!=null)
		{
			for(int i=0; i<bindings.length; i++)
			{
				RequiredServiceInfo rsi = (RequiredServiceInfo)sermap.get(bindings[i].getName());
				RequiredServiceInfo newrsi = new RequiredServiceInfo(rsi.getName(), rsi.getType().getType(cl, model.getAllImports()), rsi.isMultiple(), 
					new RequiredServiceBinding(bindings[i]), ms[i].getNFRProperties(), ms[i].getTags());
				sermap.put(rsi.getName(), newrsi);
			}
		}
		
		RequiredServiceInfo[]	rservices	= (RequiredServiceInfo[])sermap.values().toArray(new RequiredServiceInfo[sermap.size()]);
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
	public boolean	hasUserBody()
	{
		return false;
	}
	
	/**
	 *  Called when the feature is shutdowned.
	 */
	public IFuture<Void> shutdown()
	{
		// Remove the persistent queries
		ServiceRegistry.getRegistry(component).removeQueries(getComponent().getComponentIdentifier());
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
			}
		}
	}
	
	//-------- IComponentFeature interface / instance level --------
	
	//-------- accessors for declared services --------
	
	/**
	 *  Resolve a declared required service of a given name.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public <T> IFuture<T> getService(String name)
	{
		return searchService(getServiceQuery(getServiceInfo(name)));
	}
	
	/**
	 *  Resolve a required service of a given type.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param type The service type.
	 *  @return The service.
	 */
	public <T> IFuture<T> getService(Class<T> type)
	{
		return searchService(getServiceQuery(getServiceInfo(type)));
	}
	
	/**
	 *  Resolve a required services of a given name.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param name The services name.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> getServices(String name)
	{
		return searchServices(getServiceQuery(getServiceInfo(name)));
	}
	
	/**
	 *  Resolve a required services of a given type.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param type The services type.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> getServices(Class<T> type)
	{
		return searchServices(getServiceQuery(getServiceInfo(type)));
	}
	
	/**
	 *  Resolve a declared required service of a given name.
	 *  Synchronous method only for locally available services.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public <T> T getLocalService(String name)
	{
		return searchLocalService(getServiceQuery(getServiceInfo(name)));
	}
	
	/**
	 *  Resolve a required service of a given type.
	 *  Synchronous method only for locally available services.
	 *  @param type The service type.
	 *  @return The service.
	 */
	public <T> T getLocalService(Class<T> type)
	{
		return searchLocalService(getServiceQuery(getServiceInfo(type)));
	}
	
	/**
	 *  Resolve a required services of a given name.
	 *  Synchronous method only for locally available services.
	 *  @param name The services name.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> Collection<T> getLocalServices(String name)
	{
		return searchLocalServices(getServiceQuery(getServiceInfo(name)));		
	}
	
	/**
	 *  Resolve a required services of a given type.
	 *  Synchronous method only for locally available services.
	 *  @param type The services type.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> Collection<T> getLocalServices(Class<T> type)
	{
		return searchLocalServices(getServiceQuery(getServiceInfo(type)));
	}
	
	//-------- methods for searching --------
	
	/**
	 *  Search for matching services and provide first result.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> IFuture<T> searchService(ServiceQuery<T> query)
	{
		final Future<T>	fut	= new Future<T>();
		// TODO: global registry search.
		???.addResultListener(
			getComponent().getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<T>(fut)
		{
			public void customResultAvailable(Object result)
			{
				fut.setResult((T)BasicServiceInvocationHandler.createRequiredServiceProxy(getComponent(), 
					(IService)result, null, new RequiredServiceInfo(type), null, Starter.isRealtimeTimeout(getComponent().getComponentIdentifier())));
			}
		}));
		
		//
		return fut;
	}
	
	/**
	 *  Search for matching services and provide first result.
	 *  Synchronous method only for locally available services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> T searchLocalService(ServiceQuery<T> query)
	{
//		if(shutdowned)
//		{
//			return new Future<T>(new ComponentTerminatedException(id));
//		}

		T ret	=  ServiceRegistry.getRegistry(getComponent()).searchService(query);
		ret	= BasicServiceInvocationHandler.createRequiredServiceProxy(getComponent(), 
			(IService)ret, null, new RequiredServiceInfo(type), null, Starter.isRealtimeTimeout(getComponent().getComponentIdentifier()))
		
		// TODO: proxy for service itself (was in service fetcher)
		return ret;
	}
	
	/**
	 *  Search for all matching services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding services or ServiceNotFoundException when not found.
	 */
	public <T>  ITerminableIntermediateFuture<T> searchServices(ServiceQuery<T> query)
	{
		// Todo: terminable?
		final TerminableIntermediateFuture<T>	fut	= new TerminableIntermediateFuture<T>();
		SServiceProvider.getServices(getComponent(), type).addResultListener(new IntermediateDelegationResultListener<T>(fut)
		{
			// Not necessary any longer
//			public void customIntermediateResultAvailable(Object result)
//			{
//				fut.addIntermediateResult((T)BasicServiceInvocationHandler.createRequiredServiceProxy(getComponent(),
//					(IService)result, null, new RequiredServiceInfo(type), null, Starter.isRealtimeTimeout(getComponent().getComponentIdentifier())));
//			}
		});
		return (ITerminableIntermediateFuture<T>)FutureFunctionality.getDelegationFuture(fut, new ComponentFutureFunctionality(getComponent()));
	}
	
	/**
	 *  Search for all matching services.
	 *  Synchronous method only for locally available services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding services or ServiceNotFoundException when not found.
	 */
	public <T> Collection<T> searchLocalServices(ServiceQuery<T> query);
	
	//-------- query methods --------

	/**
	 *  Add a service query.
	 *  Continuously searches for matching services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(ServiceQuery<T> query);

	
	//-------- old --------
	
	/**
	 *  Get a required service info.
	 *  @return The required service info.
	 */
	public RequiredServiceInfo getRequiredServiceInfo(String name)
	{
//		if(shutdowned)
//			throw new ComponentTerminatedException(id);

		return requiredserviceinfos==null? null: (RequiredServiceInfo)requiredserviceinfos.get(name);
	}
	
	/**
	 *  Get the required services.
	 *  @return The required services.
	 */
	public RequiredServiceInfo[] getRequiredServiceInfos()
	{
//		if(shutdowned)
//			throw new ComponentTerminatedException(id);

		return requiredserviceinfos==null? new RequiredServiceInfo[0]: 
			(RequiredServiceInfo[])requiredserviceinfos.values().toArray(new RequiredServiceInfo[requiredserviceinfos.size()]);
	}
	
	/**
	 *  Set the required services.
	 *  @param required services The required services to set.
	 */
	public void setRequiredServiceInfos(RequiredServiceInfo[] requiredservices)
	{
//		if(shutdowned)
//			throw new ComponentTerminatedException(id);

		this.requiredserviceinfos = null;
		addRequiredServiceInfos(requiredservices);
	}

	/**
	 *  Get a multi service.
	 *  @param reqname The required service name.
	 *  @param multitype The interface of the multi service.
	 */
	public <T> T getMultiService(String reqname, Class<T> multitype)
	{
		return (T)ProxyFactory.newProxyInstance(getComponent().getClassLoader(), new Class[]{multitype}, 
			new MultiServiceInvocationHandler(getComponent(), reqname, multitype));
	}
	
	
	/**
	 *  Add a service query to the registry.
	 *  @param type The service type.
	 *  @param scope The scope.
	 *  @param filter The filter.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(Class<T> type, String scope, IAsyncFilter<T> filter)
	{
		ServiceQuery<T> query = new ServiceQuery<T>(type, scope, null, getComponent().getComponentIdentifier(), filter);
		@SuppressWarnings("unchecked")
		ISubscriptionIntermediateFuture<T>	ret	= (ISubscriptionIntermediateFuture<T>)FutureFunctionality.getDelegationFuture(
			ServiceRegistry.getRegistry(getComponent()).addQuery(query), new ComponentFutureFunctionality(getComponent()));
		return ret;
	}

	/**
	 *  Listen to service call events (call, result and commands).
	 */
	// Todo: only match specific calls?
	// Todo: Commands
	public ISubscriptionIntermediateFuture<ServiceCallEvent>	getServiceEvents()
	{
		if(subscriptions==null)
		{
			subscriptions	= new LinkedHashSet<SubscriptionIntermediateFuture<ServiceCallEvent>>();
		}
		@SuppressWarnings("unchecked")
		final SubscriptionIntermediateFuture<ServiceCallEvent>	ret	= (SubscriptionIntermediateFuture<ServiceCallEvent>)
			SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, getComponent());
		ret.setTerminationCommand(new TerminationCommand()
		{
			@Override
			public void terminated(Exception reason)
			{
				subscriptions.remove(ret);
				if(subscriptions.isEmpty())
				{
					subscriptions	= null;
				}
			}
		});
		subscriptions.add(ret);
		return ret;
	}
	
	/**
	 *  Post a service call event.
	 */
	public void	postServiceEvent(ServiceCallEvent event)
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
	
	//-------- impl methods --------
	
	/**
	 *  Search for matching services and provide first result.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> IFuture<T> resolveService(ServiceQuery<T> query, RequiredServiceInfo info)
	{
//		if(shutdowned)
//		{
//			return new Future<T>(new ComponentTerminatedException(id));
//		}
		final Future<T>	fut	= new Future<T>();
		
		// When service not declared (i.e. search) -> create matching info from query.
		RequiredServiceInfo	finfo	= info!=null ? info : createServiceInfo(query);
		
		// TODO: global registry search.
		IFuture<T>	search	= new Future<>(new UnsupportedOperationException("TODO"));
		
		search.addResultListener(
			// Component result listener for result always on component thread
			getComponent().getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<T>(fut)
		{
			public void customResultAvailable(T service)
			{
				// Wrap result in proxy, if required. 
				fut.setResult(createServiceProxy(service, finfo));
			}
		}));
		
		return fut;
	}
	
	/**
	 *  Search for matching services and provide first result.
	 *  Synchronous method only for locally available services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> T resolveLocalService(ServiceQuery<T> query, RequiredServiceInfo info)
	{
//		if(shutdowned)
//		{
//			return new Future<T>(new ComponentTerminatedException(id));
//		}

		// When service not declared (i.e. search) -> create matching info from query.
		RequiredServiceInfo	finfo	= info!=null ? info : createServiceInfo(query);
		
		T ret	=  ServiceRegistry.getRegistry(getComponent()).searchService(query);
		
		// Wrap result in proxy, if required. 
		ret	= createServiceProxy(ret, info);
		
		// TODO: proxy for service itself (was in service fetcher)
		return ret;
	}
	
	/**
	 *  Search for all matching services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding services or ServiceNotFoundException when not found.
	 */
	public <T>  ITerminableIntermediateFuture<T> searchServices(ServiceQuery<T> query)
	{
		// Todo: terminable?
		final TerminableIntermediateFuture<T>	fut	= new TerminableIntermediateFuture<T>();
		SServiceProvider.getServices(getComponent(), type).addResultListener(new IntermediateDelegationResultListener<T>(fut)
		{
			// Not necessary any longer
//			public void customIntermediateResultAvailable(Object result)
//			{
//				fut.addIntermediateResult((T)BasicServiceInvocationHandler.createRequiredServiceProxy(getComponent(),
//					(IService)result, null, new RequiredServiceInfo(type), null, Starter.isRealtimeTimeout(getComponent().getComponentIdentifier())));
//			}
		});
		return (ITerminableIntermediateFuture<T>)FutureFunctionality.getDelegationFuture(fut, new ComponentFutureFunctionality(getComponent()));
	}
	
	/**
	 *  Search for all matching services.
	 *  Synchronous method only for locally available services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding services or ServiceNotFoundException when not found.
	 */
	public <T> Collection<T> searchLocalServices(ServiceQuery<T> query);
	
	//-------- helper methods --------
	
	/**
	 *  Map required service declaration to service query.
	 */
	protected <T> ServiceQuery<T>	getServiceQuery(RequiredServiceInfo info)
	{
		return new ServiceQuery<T>(info.getType(), info.getDefaultBinding().getScope(), null, getComponent().getComponentIdentifier());
	}
	
	/**
	 *  Create required service info from service query.
	 */
	protected <T> RequiredServiceInfo	createServiceInfo(ServiceQuery<T> query)
	{
		return new RequiredServiceInfo(null, query.getServiceType(), query.isMultiple(), null, null, Arrays.asList(query.getServiceTags());
	}
	
	/**
	 *  Get the required service info for a name.
	 *  @param name	The required service name.
	 */
	protected RequiredServiceInfo	getServiceInfo(String name)
	{
		RequiredServiceInfo	info	= requiredserviceinfos.get(name);
		if(info==null)
		{
			throw new IllegalArgumentException("No such required service: "+name);
		}
		return info;
	}
	
	/**
	 *  Get the required service info for a type.
	 *  @param type	The required service type.
	 */
	protected RequiredServiceInfo	getServiceInfo(Class<?> type)
	{
		RequiredServiceInfo	info	= requiredserviceinfos.get(type.getName());
		if(info==null)
		{
			throw new IllegalArgumentException("No unique required service found for type: "+type);
		}
		return info;
	}
	
	/**
	 *  Create a required service proxy
	 */
	protected <T>	T	createServiceProxy(T service, RequiredServiceInfo info)
	{
		@SuppressWarnings("unchecked")
		T ret	= (T)BasicServiceInvocationHandler.createRequiredServiceProxy(getComponent(), 
			(IService)service, null, info, null, Starter.isRealtimeTimeout(getComponent().getComponentIdentifier()));
		return ret;
	}
}
