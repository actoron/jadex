package jadex.bridge.service.component;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IRequiredServiceFetcher;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.component.multiinvoke.MultiServiceInvocationHandler;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.search.TagFilter;
import jadex.commons.IAsyncFilter;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.TerminableIntermediateFuture;

/**
 *  Feature for provided services.
 */
// Todo: synchronous or asynchronous (for search)?
public class RequiredServicesComponentFeature	extends AbstractComponentFeature implements IRequiredServicesFeature
{
	//-------- attributes --------
	
	/** The service fetch method table (name -> fetcher). */
	protected Map<String, IRequiredServiceFetcher>	reqservicefetchers;

	/** The required service infos. */
	protected Map<String, RequiredServiceInfo> requiredserviceinfos;
	
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
				ms[i].getMultiplexType()==null? null: ms[i].getMultiplexType().getType(cl, model.getAllImports()), ms[i].getDefaultBinding(), ms[i].getNFRProperties(), ms[i].getTags());
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
					ms[i].getMultiplexType()==null? null: ms[i].getMultiplexType().getType(cl, model.getAllImports()), new RequiredServiceBinding(cs[i].getDefaultBinding()), ms[i].getNFRProperties(), ms[i].getTags());
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
					rsi.getMultiplexType()==null? null: rsi.getMultiplexType().getType(cl, model.getAllImports()), new RequiredServiceBinding(bindings[i]), ms[i].getNFRProperties(), ms[i].getTags());
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
		ServiceRegistry.getRegistry(component).removeQueries(getId());
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
	 *  Get a required service of a given name.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name)
	{
		return getRequiredService(name, false);
	}
	
	/**
	 *  Get a required services of a given name.
	 *  @param name The services name.
	 *  @return The service.
	 */
	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name)
	{
		return getRequiredServices(name, false);
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name, boolean rebind)
	{
		return getRequiredService(name, rebind, (IAsyncFilter<T>)null);
	}
	
	/**
	 *  Get a required services.
	 *  @return The services.
	 */
	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name, boolean rebind)
	{
		return getRequiredServices(name, rebind, (IAsyncFilter<T>)null);
	}
	
//	/**
//	 *  Get a required service of a given name.
//	 *  @param name The service name.
//	 *  @return The service.
//	 */
//	public <T> T getLocalRequiredService(String name)
//	{
//		return getLocalRequiredService(name, false);
//	}
//	
//	/**
//	 *  Get a required services of a given name.
//	 *  @param name The services name.
//	 *  @return Each service as an intermediate result and a collection of services as final result.
//	 */
//	public <T> T getLocalRequiredServices(String name)
//	{
//		return getLocalRequiredServices(name, false);
//	}
//	
//	/**
//	 *  Get a required service.
//	 *  @return The service.
//	 */
//	public <T> T getLocalRequiredService(String name, boolean rebind)
//	{
//		RequiredServiceInfo info = getRequiredServiceInfo(name);
//		if(info==null)
//		{
//			throw new ServiceNotFoundException(name);
//		}
//	}
//	
//	/**
//	 *  Get a required services.
//	 *  @return Each service as an intermediate result and a collection of services as final result.
//	 */
//	public <T> T getLocalRequiredServices(String name, boolean rebind)
//	{
//		RequiredServiceInfo info = getRequiredServiceInfo(name);
//		if(info==null)
//		{
//			throw new ServiceNotFoundException(name);
//		}
//	}

	/**
	 *  Get a required services.
	 *  @return The services.
	 */
	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name, boolean rebind, IAsyncFilter<T> filter)
	{
//		if(shutdowned)
//			return new TerminableIntermediateFuture<T>(new ComponentTerminatedException(id));

		RequiredServiceInfo info = getRequiredServiceInfo(name);
		if(info==null)
		{
			TerminableIntermediateFuture<T> ret = new TerminableIntermediateFuture<T>();
			ret.setException(new ServiceNotFoundException(name));
			return ret;
		}
		else
		{
			RequiredServiceBinding binding = info.getDefaultBinding();//getRequiredServiceBinding(name);
			return getRequiredServices(info, binding, rebind, filter);
		}
	}
	
	/**
	 *  Get a required service using tags.
	 *  @param name The required service name.
	 *  @param rebind If false caches results.
	 *  @param tags The service tags.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name, boolean rebind, String... tags)
	{
		return getRequiredService(name, rebind, new TagFilter<T>(component.getExternalAccess(), tags));
	}
	
	/**
	 *  Get a required services using tags.
	 *  @param name The required service name.
	 *  @param rebind If false caches results.
	 *  @param tags The service tags.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name, boolean rebind, String... tags)
	{
		return getRequiredServices(name, rebind, new TagFilter<T>(component.getExternalAccess(), tags));
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
	
//	/**
//	 *  Get a required service.
//	 *  @return The service.
//	 */
//	public <T> IFuture<T> getRequiredService(RequiredServiceInfo info, RequiredServiceBinding binding)
//	{
//		return getRequiredService(info, binding, false, (IAsyncFilter)null);
//	}
	
//	/**
//	 *  Get a required service.
//	 *  @return The service.
//	 */
//	public <T> IFuture<T> getRequiredService(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind)
//	{
//		return getRequiredService(info, binding, rebind, null);
//	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	protected <T> IFuture<T> getRequiredService(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind, IAsyncFilter<T> filter)
	{
//		if(shutdowned)
//		{
//			return new Future<T>(new ComponentTerminatedException(id));
//		}

		if(info==null)
		{
			Future<T> ret = new Future<T>();
			ret.setException(new IllegalArgumentException("Info must not null."));
			return ret;
		}
		
		if(info.getMultiplexType()!=null)
		{
			T ms = getMultiService(info.getName(), (Class<T>)info.getMultiplexType().getType(getComponent().getClassLoader(), getComponent().getModel().getAllImports()));
			return new Future<T>(ms);
		}
		
//		IFuture<T>	fut	= super.getRequiredService(info, binding, rebind, filter);
		
		IRequiredServiceFetcher fetcher = getRequiredServiceFetcher(info.getName());
		IFuture<T> fut = fetcher.getService(info, binding, rebind, filter);
		
		return FutureFunctionality.getDelegationFuture(fut, new ComponentFutureFunctionality(getComponent()));
	}
	
//	/**
//	 *  Get required services.
//	 *  @return The services.
//	 */
//	public <T> IIntermediateFuture<T> getRequiredServices(RequiredServiceInfo info, RequiredServiceBinding binding)
//	{
//		return getRequiredServices(info, binding, false, (IAsyncFilter)null);
//	}
	
//	/**
//	 *  Get required services.
//	 *  @return The services.
//	 */
//	public <T> IIntermediateFuture<T> getRequiredServices(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind)
//	{
//		return getRequiredServices(info, binding, rebind, null);
//	}
	
	/**
	 *  Get required services.
	 *  @return The services.
	 */
	protected <T> ITerminableIntermediateFuture<T> getRequiredServices(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind, IAsyncFilter<T> filter)
	{
//		if(shutdowned)
//		{
//			return new TerminableIntermediateFuture<T>(new ComponentTerminatedException(id));
//		}

		if(info==null)
		{
			TerminableIntermediateFuture<T> ret = new TerminableIntermediateFuture<T>();
			ret.setException(new IllegalArgumentException("Info must not null."));
			return ret;
		}
		
		IRequiredServiceFetcher fetcher = getRequiredServiceFetcher(info.getName());
		ITerminableIntermediateFuture<T> fut = fetcher.getServices(info, binding, rebind, filter);
		
		return (ITerminableIntermediateFuture<T>)FutureFunctionality.getDelegationFuture(fut, new ComponentFutureFunctionality(getComponent()));
	}
	
//	/**
//	 *  Get a required service.
//	 *  @return The service.
//	 */
//	public <T> IFuture<T> getRequiredService(RequiredServiceInfo info, RequiredServiceBinding binding, IAsyncFilter<T> filter)
//	{
//		IFuture<T> ret = getRequiredService(info, binding, false, filter);
//		return ret;
////		return getRequiredService(info, binding, false, filter);
//	}
	
//	/**
//	 *  Get required services.
//	 *  @return The services.
//	 */
//	public <T> IIntermediateFuture<T> getRequiredServices(RequiredServiceInfo info, RequiredServiceBinding binding, IAsyncFilter<T> filter)
//	{
//		return getRequiredServices(info, binding, false, filter);
//	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name, boolean rebind, IAsyncFilter<T> filter)
	{
//		if(shutdowned)
//			return new Future<T>(new ComponentTerminatedException(id));
		
		RequiredServiceInfo info = getRequiredServiceInfo(name);
		if(info==null)
		{
			Future<T> ret = new Future<T>();
			ret.setException(new ServiceNotFoundException(name+" in: "+getComponent().getComponentIdentifier()));
			return ret;
		}
		else
		{
			RequiredServiceBinding binding = info.getDefaultBinding();//getRequiredServiceBinding(name);
			return getRequiredService(info, binding, rebind, filter);
		}
	}
	
	/**
	 *  Get the result of the last search.
	 *  @param name The required service name.
	 *  @return The last result.
	 */
	public <T> T getLastRequiredService(String name)
	{
		IRequiredServiceFetcher fetcher = getRequiredServiceFetcher(name);
		return fetcher.getLastService();
	}
	
	/**
	 *  Get the result of the last search.
	 *  @param name The required services name.
	 *  @return The last result.
	 */
	public <T> Collection<T> getLastRequiredServices(String name)
	{
		IRequiredServiceFetcher fetcher = getRequiredServiceFetcher(name);
		return fetcher.getLastServices();
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public <T> IFuture<T> searchService(final Class<T> type)
	{
		final Future<T>	fut	= new Future<T>();
		SServiceProvider.getService(getComponent(), type).addResultListener(new DelegationResultListener<T>(fut)
		{
			// Not necessary any longer
//			public void customResultAvailable(Object result)
//			{
//				fut.setResult((T)BasicServiceInvocationHandler.createRequiredServiceProxy(getComponent(), 
//					(IService)result, null, new RequiredServiceInfo(type), null, Starter.isRealtimeTimeout(getComponent().getComponentIdentifier())));
//			}
		});
		return FutureFunctionality.getDelegationFuture(fut, new ComponentFutureFunctionality(getComponent()));
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public <T> IFuture<T> searchService(final Class<T> type, String scope)
	{
//		if(type.getName().toLowerCase().indexOf("environment")!=-1)
//			System.out.println("dghfhj");
		
		final Future<T>	fut	= new Future<T>();
		SServiceProvider.getService(getComponent(), type, scope).addResultListener(new DelegationResultListener<T>(fut));
//		{
//			// Not necessary any longer
//			public void customResultAvailable(Object result)
//			{
//				fut.setResult((T)BasicServiceInvocationHandler.createRequiredServiceProxy(getComponent(), 
//					(IService)result, null, new RequiredServiceInfo(type), null, Starter.isRealtimeTimeout(getComponent().getComponentIdentifier())));
//			}
//		});
		return FutureFunctionality.getDelegationFuture(fut, new ComponentFutureFunctionality(getComponent()));
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> searchServices(final Class<T> type)
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
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> searchServices(final Class<T> type, String scope)
	{
		// Todo: terminable?
		final TerminableIntermediateFuture<T>	fut	= new TerminableIntermediateFuture<T>();
		SServiceProvider.getServices(getComponent(), type, scope).addResultListener(new IntermediateDelegationResultListener<T>(fut)
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
	 *  Get one service of a type from a specific component.
	 *  @param type The class.
	 *  @param cid The component identifier of the target component.
	 *  @return The corresponding service.
	 */
	public <T> IFuture<T> searchService(final Class<T> type, IComponentIdentifier cid)
	{
		final Future<T>	fut	= new Future<T>();
		
		SServiceProvider.getService(getComponent(), cid, type).addResultListener(new DelegationResultListener<T>(fut));
//		{
//			// Not necessary any longer (done in SServiceProvider)
//			public void customResultAvailable(Object result)
//			{
//				fut.setResult((T)BasicServiceInvocationHandler.createRequiredServiceProxy(getComponent(), 
//					(IService)result, null, new RequiredServiceInfo(type), null, Starter.isRealtimeTimeout(getComponent().getComponentIdentifier())));
//			}
//		});
		
		return FutureFunctionality.getDelegationFuture(fut, new ComponentFutureFunctionality(getComponent()));
	}
	
	/**
	 *  Get a required service fetcher.
	 *  @param name The required service name.
	 *  @return The service fetcher.
	 */
	protected IRequiredServiceFetcher getRequiredServiceFetcher(String name)
	{
		IRequiredServiceFetcher ret = reqservicefetchers!=null ? reqservicefetchers.get(name) : null;
		if(ret==null)
		{
			ret = createServiceFetcher(name);
			if(reqservicefetchers==null)
				reqservicefetchers = new HashMap<String, IRequiredServiceFetcher>();
			reqservicefetchers.put(name, ret);
		}
		return ret;
	}
	
	/**
	 *  Create a service fetcher.
	 */
	public IRequiredServiceFetcher createServiceFetcher(String name)
	{
		return new DefaultServiceFetcher(getComponent(), Starter.isRealtimeTimeout(getComponent().getComponentIdentifier()));
	}
	
	/**
	 *  Get the globally unique id of the provider.
	 *  @return The id of this provider.
	 */
	public IComponentIdentifier	getId()
	{
		return component.getComponentIdentifier();
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
		return ServiceRegistry.getRegistry(getComponent()).addQuery(query);
	}
}
