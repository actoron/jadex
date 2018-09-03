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

import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.INFPropertyComponentFeature;
import jadex.bridge.component.impl.AbstractComponentFeature;
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
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.ServiceEvent;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceQuery.Multiplicity;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.registryv2.ISearchQueryManagerService;
import jadex.bridge.service.types.registryv2.SlidingCuckooFilter;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
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
		IModelInfo model = getComponent().getModel();
		ClassLoader cl = getComponent().getClassLoader();
		String config = getComponent().getConfiguration();
		
		// Required services. (Todo: prefix for capabilities)
		RequiredServiceInfo[] ms = model.getServices();
		
		Map<String, RequiredServiceInfo> sermap = new LinkedHashMap<String, RequiredServiceInfo>();
		for(int i=0; i<ms.length; i++)
		{
			ms[i] = new RequiredServiceInfo(/*getServicePrefix()+*/ms[i].getName(), ms[i].getType().getType(cl, model.getAllImports()), ms[i].isMultiple(), 
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
				RequiredServiceInfo newrsi = new RequiredServiceInfo(rsi.getName(), rsi.getType().getType(cl, model.getAllImports()), rsi.isMultiple(), 
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
				RequiredServiceInfo newrsi = new RequiredServiceInfo(rsi.getName(), rsi.getType().getType(cl, model.getAllImports()), rsi.isMultiple(), 
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
		return resolveService(query, createServiceInfo(query));
	}
	
	/**
	 *  Search for matching services and provide first result.
	 *  Synchronous method only for locally available services.
	 *  @param query The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> T searchLocalService(ServiceQuery<T> query)
	{
		return resolveLocalService(query, createServiceInfo(query));
	}
	
	/**
	 *  Search for all matching services.
	 *  @param query The search query.
	 *  @return Future providing the corresponding services or ServiceNotFoundException when not found.
	 */
	public <T>  ITerminableIntermediateFuture<T> searchServices(ServiceQuery<T> query)
	{
		return resolveServices(query, createServiceInfo(query));
	}
	
	/**
	 *  Search for all matching services.
	 *  Synchronous method only for locally available services.
	 *  @param query The search query.
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
		return resolveQuery(query, createServiceInfo(query));
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
			return resolveLocalService(new ServiceQuery<>(type).setMultiplicity(Multiplicity.ZERO_ONE), null);
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
		return resolveLocalServices(new ServiceQuery<>(type), null);
	}

	
	//-------- impl/raw methods --------
	
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
			T t = (T)createServiceProxy(sid, info);
			ret.setResult(t);
		}
		
		// If not found -> try to find remotely
		else if(isRemote(query))
		{
			ISearchQueryManagerService sqms = searchLocalService(new ServiceQuery<>(ISearchQueryManagerService.class).setMultiplicity(Multiplicity.ZERO_ONE));
			if(sqms!=null)
			{
				@SuppressWarnings("rawtypes")
				ITerminableFuture fut = sqms.searchService(query);
				@SuppressWarnings("unchecked")
				ITerminableFuture<T> castedfut = (ITerminableFuture<T>) fut;
				ret = FutureFunctionality.getDelegationFuture(castedfut, new FutureFunctionality(getComponent().getLogger())
				{
					@Override
					public Object handleResult(Object result) throws Exception
					{
						return createServiceProxy(result, info);
					}
				}); 			
			}
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
		T ret = sid!=null ? (T)createServiceProxy(sid, info) : null;
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
		enhanceQuery(query, true);
		
		TerminableIntermediateFuture<T> ret = new TerminableIntermediateFuture<>();
		
		int[] finishcount = new int[1];
		finishcount[0] = 1;
		
		// Find remote matches
		if(isRemote(query))
		{
			ISearchQueryManagerService sqms = searchLocalService(new ServiceQuery<>(ISearchQueryManagerService.class).setMultiplicity(Multiplicity.ZERO_ONE));
			if(sqms!=null)
			{
				++finishcount[0];
				ITerminableIntermediateFuture<IServiceIdentifier> remotes = sqms.searchServices(query);
				
				remotes.addResultListener(new IIntermediateResultListener<IServiceIdentifier>()
				{

					public void resultAvailable(Collection<IServiceIdentifier> result)
					{
						finished();
					}

					public void exceptionOccurred(Exception exception)
					{
						finished();
					}
					
					public void intermediateResultAvailable(IServiceIdentifier result)
					{
						@SuppressWarnings("unchecked")
						T t = (T)createServiceProxy(result, info);
						ret.addIntermediateResultIfUndone(t);
					}

					public void finished()
					{
						--finishcount[0];
						if (finishcount[0] == 0)
							ret.setFinishedIfUndone();
					}
				});
			}
		}
		
		// Find local matches.
		IServiceRegistry registry = ServiceRegistry.getRegistry(getInternalAccess());
		Collection<IServiceIdentifier> localresults =  registry.searchServices(query);
		
		for(IServiceIdentifier result: localresults)
		{
			@SuppressWarnings("unchecked")
			T t = (T)createServiceProxy(result, info);
			ret.addIntermediateResult(t);
		}
		--finishcount[0];
		if (finishcount[0] == 0)
			ret.setFinished();
		
//		ITerminableIntermediateFuture<T> ret = null;
//		
//		// Find remote matches
//		if(isRemote(query))
//		{
//			ISearchQueryManagerService sqms = searchLocalService(new ServiceQuery<>(ISearchQueryManagerService.class).setMultiplicity(Multiplicity.ZERO_ONE));
//			if(sqms!=null)
//			{
//				@SuppressWarnings("rawtypes")
//				ITerminableIntermediateFuture remotes = sqms.searchServices(query);
//				@SuppressWarnings("unchecked")
//				ITerminableIntermediateFuture<T> castedremotes = (ITerminableIntermediateFuture<T>) remotes;
//				Future<Collection<T>> fut = FutureFunctionality.getDelegationFuture(castedremotes, new FutureFunctionality(getComponent().getLogger())
//				{
//					@Override
//					public Object handleIntermediateResult(Object result) throws Exception
//					{
//						return createServiceProxy(result, info);
//					}
//				});
//				
//				@SuppressWarnings("unchecked")
//				ITerminableIntermediateFuture<T> tmp = (ITerminableIntermediateFuture<T>)fut;
//				ret = tmp;
//			}
//		}
//		
//		// Find local matches.
//		IServiceRegistry registry = ServiceRegistry.getRegistry(getComponent());
//		Collection<IServiceIdentifier> localresults =  registry.searchServices(query);
//		
//		// No remote matches -> create simple result future.
//		if(ret==null)
//		{
//			TerminableIntermediateFuture<T> fut = new TerminableIntermediateFuture<>();
//			for(IServiceIdentifier result: localresults)
//			{
//				@SuppressWarnings("unchecked")
//				T t = (T)createServiceProxy(result, info);
//				fut.addIntermediateResult(t);
//			}
//			fut.setFinished();
//			ret = fut;
//		}
//		
//		// Merge remote and local matches using delegation future functionality  (on same thread, thus local before remote results, if any)
//		else
//		{
//			@SuppressWarnings("unchecked")
//			IntermediateFuture<T> fut = (IntermediateFuture<T>)ret;
//			
//			for(IServiceIdentifier result: localresults)
//			{
//				@SuppressWarnings("unchecked")
//				T t = (T)result; // Hack!!! Isn't really <T> but ignored at runtime anyways and converted by future functionality
//				fut.addIntermediateResultIfUndone(t);
//			}
//		}
//				
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
			T service = (T)createServiceProxy(result, info);
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
		
		// Query remote
		ISearchQueryManagerService sqms = searchLocalService(new ServiceQuery<>(ISearchQueryManagerService.class).setMultiplicity(Multiplicity.ZERO_ONE));
		ISubscriptionIntermediateFuture<T> remotes = isRemote(query) && sqms!=null ? sqms.addQuery(query) : null;
		
		// Query local registry
		IServiceRegistry registry = ServiceRegistry.getRegistry(getInternalAccess());
		ISubscriptionIntermediateFuture<?> localresults =  (ISubscriptionIntermediateFuture<?>)registry.addQuery(query);
		@SuppressWarnings({"unchecked", "rawtypes"})
		ISubscriptionIntermediateFuture<T> ret = (ISubscriptionIntermediateFuture)FutureFunctionality
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
					scf.insert(result.toString());
					return createServiceProxy(result, info);
				}
			}
			
			@Override
			public void handleTerminated(Exception reason)
			{
				// TODO: multi delegation future with multiple sources but one target?
				if(remotes!=null)
					remotes.terminate(reason);
				
				super.handleTerminated(reason);
			}
		});
		
		// Add remote results to future (functionality handles wrapping)
		if(remotes!=null)
		{
			remotes.addIntermediateResultListener(
				result->
				{
					@SuppressWarnings("unchecked")
					IntermediateFuture<T> fut = (IntermediateFuture<T>)ret;
					fut.addIntermediateResult(result);
				},
				exception -> {}); // Ignore exception (printed when no listener supplied)
		}
		
		return ret;
	}
	
	//-------- helper methods --------
	
	/**
	 * When searching for declared service -> map required service declaration to service query.
	 */
	protected <T> ServiceQuery<T> getServiceQuery(RequiredServiceInfo info)
	{
		// TODO???
//		info.getNFRProperties();
//		info.getDefaultBinding().getComponentName();
//		info.getDefaultBinding().getComponentType();
		
		ServiceQuery<T> ret = new ServiceQuery<T>(info.getType(), info.getDefaultBinding().getScope(), getComponent().getId());
		ret.setMultiplicity(info.isMultiple() ? Multiplicity.ZERO_MANY : Multiplicity.ONE);
		
		if(info.getTags()!=null)
			ret.setServiceTags(info.getTags().toArray(new String[info.getTags().size()]), getComponent().getExternalAccess());
		
		return ret;
	}
	
	/**
	 *  When searching with query -> create required service info from service query.
	 */
	protected <T> RequiredServiceInfo createServiceInfo(ServiceQuery<T> query)
	{
		// TODO: multiplicity required here for info? should not be needed for proxy creation
		return new RequiredServiceInfo(null, query.getServiceType(), false, null, null, query.getServiceTags()==null ? null : Arrays.asList(query.getServiceTags()));
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
	protected Object createServiceProxy(Object service, RequiredServiceInfo info)
	{
		ServiceEvent event = null;
		if (service instanceof ServiceEvent)
		{
			event = (ServiceEvent) service;
			service = event.getService();
		}
		
		// If service identifier -> find/create service object or proxy
		if(service instanceof IServiceIdentifier)
		{
			IServiceIdentifier sid = (IServiceIdentifier)service;
			
			// Local component -> fetch local service object.
			if(sid.getProviderId().getRoot().equals(getComponent().getId().getRoot()))
			{
				service = ServiceRegistry.getRegistry(getInternalAccess()).getLocalService(sid); 			
			}
			
			// Remote component -> create remote proxy
			else
			{
				service = RemoteMethodInvocationHandler.createRemoteServiceProxy(getInternalAccess(), sid);
			}
		}
		
		// else service event -> just return event, as desired by user (specified in query return type)
		

		// Add required service proxy if specified.
		if(service instanceof IService && info!=null)
		{
			service = BasicServiceInvocationHandler.createRequiredServiceProxy(getInternalAccess(), 
				(IService)service, null, info, info.getDefaultBinding(), Starter.isRealtimeTimeout(getComponent().getId()));
			
			
			// Check if no property provider has been created before and then create and init properties
			if(!getComponent().getFeature(INFPropertyComponentFeature.class).hasRequiredServicePropertyProvider(((IService)service).getId()))
			{
				INFMixedPropertyProvider nfpp = getComponent().getFeature(INFPropertyComponentFeature.class)
					.getRequiredServicePropertyProvider(((IService)service).getId());
				
				List<NFRPropertyInfo> nfprops = info.getNFRProperties();
				if(nfprops!=null && nfprops.size()>0)
				{
					for(NFRPropertyInfo nfprop: nfprops)
					{
						MethodInfo mi = nfprop.getMethodInfo();
						Class<?> clazz = nfprop.getClazz().getType(getComponent().getClassLoader(), getComponent().getModel().getAllImports());
						INFProperty<?, ?> nfp = AbstractNFProperty.createProperty(clazz, getInternalAccess(), (IService)service, nfprop.getMethodInfo(), nfprop.getParameters());
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
		
		if (event != null)
		{
			event.setService(service);
			service = event;
		}
		
		return service;
	}
	

	/**
	 *  Enhance a query before processing.
	 *  Does some necessary preprocessing and needs to be called at least once before processing the query.
	 *  @param query The query to be enhanced.
	 */
	protected <T> void enhanceQuery(ServiceQuery<T> query, boolean multi)
	{
//		if(shutdowned)
//		{
//			return new Future<T>(new ComponentTerminatedException(id));
//		}

		// Set owner if not set
		if(query.getOwner()==null)
		{
			query.setOwner(getComponent().getId());
		}
		
		// Set scope if not set
		if(query.getScope()==null)
		{
			// Default to application if service type not set or not system service
			query.setScope(query.getServiceType()!=null && ServiceIdentifier.isSystemService(query.getServiceType().getType(getComponent().getClassLoader()))
				? RequiredServiceInfo.SCOPE_PLATFORM : RequiredServiceInfo.SCOPE_APPLICATION);
		}
		
		if(query.getMultiplicity()==null)
		{
			// Fix multiple flag according to single/multi method 
			query.setMultiplicity(multi ? Multiplicity.ZERO_MANY : Multiplicity.ONE);
		}
		
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
			|| !RequiredServiceInfo.isScopeOnLocalPlatform(query.getScope());
	}
}
