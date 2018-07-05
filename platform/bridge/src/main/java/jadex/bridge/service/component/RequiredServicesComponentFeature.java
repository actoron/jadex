package jadex.bridge.service.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.ServiceIdentifier;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.commons.future.TerminationCommand;

/**
 *  Feature for provided services.
 */
public class RequiredServicesComponentFeature	extends AbstractComponentFeature implements IRequiredServicesFeature, IInternalServiceMonitoringFeature
{
	/** Marker for duplicate declarations of same type. */
	private static final RequiredServiceInfo	ISS_MEHR_WURST	= new RequiredServiceInfo();
	
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
		RequiredServiceInfo[] ms = model.getServices();
		
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
			RequiredServiceInfo[] cs = cinfo.getServices();
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
		ServiceRegistry.getRegistry(component).removeQueries(getComponent().getIdentifier());
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
						this.requiredserviceinfos.put(requiredservices[i].getType().getTypeName(), ISS_MEHR_WURST);
					}
					else
					{
						this.requiredserviceinfos.put(requiredservices[i].getType().getTypeName(), requiredservices[i]);
					}
				}
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
		RequiredServiceInfo	info	= getServiceInfo(type);
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
		RequiredServiceInfo	info	= getServiceInfo(type);
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
		RequiredServiceInfo	info	= getServiceInfo(type);
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
		RequiredServiceInfo	info	= getServiceInfo(type);
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
	 *  @param query	The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> IFuture<T> searchService(ServiceQuery<T> query)
	{
		return resolveService(query, createServiceInfo(query));
	}
	
	/**
	 *  Search for matching services and provide first result.
	 *  Synchronous method only for locally available services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> T searchLocalService(ServiceQuery<T> query)
	{
		return resolveLocalService(query, createServiceInfo(query));
	}
	
	/**
	 *  Search for all matching services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding services or ServiceNotFoundException when not found.
	 */
	public <T>  ITerminableIntermediateFuture<T> searchServices(ServiceQuery<T> query)
	{
		return resolveServices(query, createServiceInfo(query));
	}
	
	/**
	 *  Search for all matching services.
	 *  Synchronous method only for locally available services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding services or ServiceNotFoundException when not found.
	 */
	public <T> Collection<T> searchLocalServices(ServiceQuery<T> query)
	{
		return resolveLocalServices(query, createServiceInfo(query));
	}
	
	//-------- query methods --------

	/**
	 *  Add a query for a declared required service.
	 *  Continuously searches for matching services.
	 *  @param name The name of the required service declaration.
	 *  @return Future providing the corresponding services as intermediate results.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(String name)
	{
		// TODO: global registry query.
		return new SubscriptionIntermediateFuture<>(new UnsupportedOperationException("TODO"));
	}

	/**
	 *  Add a query for a declared required service.
	 *  Continuously searches for matching services.
	 *  @param type The type of the required service declaration.
	 *  @return Future providing the corresponding services as intermediate results.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(Class<T> type)
	{
		// TODO: global registry query.
		return new SubscriptionIntermediateFuture<>(new UnsupportedOperationException("TODO"));		
	}

	/**
	 *  Add a service query.
	 *  Continuously searches for matching services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(ServiceQuery<T> query)
	{
		// TODO: global registry query.
		return new SubscriptionIntermediateFuture<>(new UnsupportedOperationException("TODO"));
	}
	
	//-------- event interface --------
	
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
	protected <T> IFuture<T> resolveService(ServiceQuery<T> query, RequiredServiceInfo info)
	{
		enhanceQuery(query, false);
		
		// When service not declared (i.e. search) -> create matching info from query.
		RequiredServiceInfo	finfo	= info!=null ? info : createServiceInfo(query);
		
		// TODO: global registry search.
		IFuture<T>	search	= new Future<>(new UnsupportedOperationException("TODO"));
		
		// Schedule result on component thread and wrap result in proxy, if required
		return FutureFunctionality.getDelegationFuture(search,
			new ComponentFutureFunctionality(getComponent())
		{
			@Override
			public Object handleResult(Object result) throws Exception
			{
				return createServiceProxy(result, finfo);
			}
		});
	}
	
	/**
	 *  Search for matching services and provide first result.
	 *  Synchronous method only for locally available services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	protected <T> T resolveLocalService(ServiceQuery<T> query, RequiredServiceInfo info)
	{
		enhanceQuery(query, false);
		
		// When service not declared (i.e. search) -> create matching info from query.
		info	= info!=null ? info : createServiceInfo(query);
		
		@SuppressWarnings("unchecked")
		T ret	=  (T)ServiceRegistry.getRegistry(getComponent())
			.getLocalService(ServiceRegistry.getRegistry(getComponent()).searchService(query));
		
		if(ret==null)
		{
			// TODO 0..1 vs 1 multiplicity
			throw new ServiceNotFoundException(query.toString());
		}
		
		// Wraps result in proxy, if required. 
		return createServiceProxy(ret, info);
	}
	
	/**
	 *  Search for all matching services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding services or ServiceNotFoundException when not found.
	 */
	protected <T>  ITerminableIntermediateFuture<T> resolveServices(ServiceQuery<T> query, RequiredServiceInfo info)
	{
		enhanceQuery(query, true);
		
		// When service not declared (i.e. search) -> create matching info from query.
		RequiredServiceInfo	finfo	= info!=null ? info : createServiceInfo(query);
		
		// TODO: global registry search.
		ITerminableIntermediateFuture<T>	search	= new TerminableIntermediateFuture<>(new UnsupportedOperationException("TODO"));
		
		// Schedule result on component thread and wrap result in proxy, if required
		@SuppressWarnings("unchecked")
		ITerminableIntermediateFuture<T>	ret	= (ITerminableIntermediateFuture<T>)FutureFunctionality.getDelegationFuture(search,
			new ComponentFutureFunctionality(getComponent())
		{
			@Override
			public Object handleIntermediateResult(Object result) throws Exception
			{
				return createServiceProxy(result, finfo);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Search for all matching services.
	 *  Synchronous method only for locally available services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding services or ServiceNotFoundException when not found.
	 */
	protected <T> Collection<T> resolveLocalServices(ServiceQuery<T> query, RequiredServiceInfo info)
	{
		enhanceQuery(query, true);
		
		// When service not declared (i.e. search) -> create matching info from query.
		info	= info!=null ? info : createServiceInfo(query);
		IServiceRegistry	registry	= ServiceRegistry.getRegistry(getComponent());
		
		Collection<IServiceIdentifier> results	=  registry.searchServices(query);
		
		// Wraps result in proxy, if required. 
		Collection<T>	ret	= new ArrayList<>();
		for(IServiceIdentifier result: results)
		{
			@SuppressWarnings("unchecked")
			T	service	= (T)createServiceProxy(registry.getLocalService(result), info);
			ret.add(service);
		}
		
		return ret;
	}
	
	//-------- helper methods --------
	
	/**
	 * When searching for declared service -> map required service declaration to service query.
	 */
	protected <T> ServiceQuery<T>	getServiceQuery(RequiredServiceInfo info)
	{
		return new ServiceQuery<T>(info.getType(), info.getDefaultBinding().getScope(), null, getComponent().getIdentifier());
	}
	
	/**
	 *  When searching with query -> create required service info from service query.
	 */
	protected <T> RequiredServiceInfo	createServiceInfo(ServiceQuery<T> query)
	{
		return new RequiredServiceInfo(null, query.getServiceType(), query.isMultiple(), null, null, query.getServiceTags()==null ? null : Arrays.asList(query.getServiceTags()));
	}
	
	/**
	 *  Get the required service info for a name.
	 *  @param name	The required service name.
	 */
	// Hack!!! used by multi invoker?
	public RequiredServiceInfo	getServiceInfo(String name)
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
		RequiredServiceInfo	info	= requiredserviceinfos.get(SReflect.getClassName(type));
		if(info==ISS_MEHR_WURST)
		{
			throw new IllegalArgumentException("Multiple required service declarations found for type: "+type);
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
			(IService)service, null, info, null, Starter.isRealtimeTimeout(getComponent().getIdentifier()));
		return ret;
	}
	

	/**
	 *  Enhance a query before processing.
	 *  Does some necessary preprocessing and needs to be called at least once before processing the query,
	 *  e.g. at start of a registry method or before sending a remote query.
	 *  Safe to be called multiple times, only the first time will have effect.
	 *  @param query	The query to be enhanced.
	 */
	protected <T> void	enhanceQuery(ServiceQuery<T> query, boolean multi)
	{
//		if(shutdowned)
//		{
//			return new Future<T>(new ComponentTerminatedException(id));
//		}

		// Check if owner set to wrong component
		if(query.getOwner()!=null && !getComponent().getIdentifier().equals(query.getOwner()))
		{
			throw new IllegalArgumentException("Query owner must be local component: "+query);
		}
		query.setOwner(getComponent().getIdentifier());
		
		// Set networks if not set for remote queries
		// TODO: more extensible way of checking for remote query
		if(query.isRemote())
		{
			// Fix multiple flag according to single/multi method 
			if(multi && !query.isMultiple())
			{
				query.setMultiple(true);
			}
			else if(query.isMultiple() && !multi)
			{
				throw new IllegalStateException("Multi query for single method: "+query);
			}
			
			// Network names not set by user?
			if(Arrays.equals(query.getNetworkNames(), ServiceQuery.NETWORKS_NOT_SET))
			{
				// Unrestricted?
				if(Boolean.TRUE.equals(query.isUnrestricted())
					|| query.getServiceType()!=null && ServiceIdentifier.isUnrestricted(getComponent(), query.getServiceType().getType(getComponent().getClassLoader()))) 
				{
					// Unrestricted -> Don't check networks.
					query.setNetworkNames((String[])null);
				}
				else
				{
					// Not unrestricted -> only find services from my local networks
					@SuppressWarnings("unchecked")
					Set<String> nnames = (Set<String>)Starter.getPlatformValue(getComponent().getIdentifier(), Starter.DATA_NETWORKNAMESCACHE);
					query.setNetworkNames(nnames!=null? nnames.toArray(new String[0]): SUtil.EMPTY_STRING_ARRAY);
				}
			}
		}
		
		// Disable local checks by default
		else if(Arrays.equals(query.getNetworkNames(), ServiceQuery.NETWORKS_NOT_SET))
		{
			query.setNetworkNames((String[])null);
		}			
	}
}
