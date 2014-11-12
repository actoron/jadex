package jadex.bridge.service.component;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.service.IRequiredServiceFetcher;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.component.multiinvoke.MultiServiceInvocationHandler;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.commons.IAsyncFilter;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.TerminableIntermediateFuture;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
	
	//-------- IComponentFeature interface / instance level --------
		
	/**
	 *  Add required services for a given prefix.
	 *  @param prefix The name prefix to use.
	 *  @param required services The required services to set.
	 */
	public void addRequiredServiceInfos(RequiredServiceInfo[] requiredservices)
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
		return getRequiredService(name, rebind, null);
	}
	
	/**
	 *  Get a required services.
	 *  @return The services.
	 */
	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name, boolean rebind)
	{
		return getRequiredServices(name, rebind, null);
	}
	
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
	 *  Get a multi service.
	 *  @param reqname The required service name.
	 *  @param multitype The interface of the multi service.
	 */
	public <T> T getMultiService(String reqname, Class<T> multitype)
	{
		return (T)Proxy.newProxyInstance(getComponent().getClassLoader(), new Class[]{multitype}, 
			new MultiServiceInvocationHandler(getComponent(), reqname, multitype));
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(RequiredServiceInfo info, RequiredServiceBinding binding)
	{
		return getRequiredService(info, binding, false, (IAsyncFilter)null);
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind)
	{
		return getRequiredService(info, binding, rebind, null);
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind, IAsyncFilter<T> filter)
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
	
	/**
	 *  Get required services.
	 *  @return The services.
	 */
	public <T> IIntermediateFuture<T> getRequiredServices(RequiredServiceInfo info, RequiredServiceBinding binding)
	{
		return getRequiredServices(info, binding, false, (IAsyncFilter)null);
	}
	
	/**
	 *  Get required services.
	 *  @return The services.
	 */
	public <T> IIntermediateFuture<T> getRequiredServices(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind)
	{
		return getRequiredServices(info, binding, rebind, null);
	}
	
	/**
	 *  Get required services.
	 *  @return The services.
	 */
	public <T> ITerminableIntermediateFuture<T> getRequiredServices(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind, IAsyncFilter<T> filter)
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
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(RequiredServiceInfo info, RequiredServiceBinding binding, IAsyncFilter<T> filter)
	{
		IFuture<T> ret = getRequiredService(info, binding, false, filter);
		return ret;
//		return getRequiredService(info, binding, false, filter);
	}
	
	/**
	 *  Get required services.
	 *  @return The services.
	 */
	public <T> IIntermediateFuture<T> getRequiredServices(RequiredServiceInfo info, RequiredServiceBinding binding, IAsyncFilter<T> filter)
	{
		return getRequiredServices(info, binding, false, filter);
	}
	
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
	public <T> IFuture<T> searchService(Class<T> type)
	{
		return SServiceProvider.getService(getComponent(), type);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public <T> IFuture<T> searchService(Class<T> type, String scope)
	{
		return SServiceProvider.getService(getComponent(), type, scope);
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> searchServices(Class<T> type)
	{
		return SServiceProvider.getServices(getComponent(), type);
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> searchServices(Class<T> type, String scope)
	{
		return SServiceProvider.getServices(getComponent(), type, scope);
	}
	
	/**
	 *  Get one service of a type from a specific component.
	 *  @param type The class.
	 *  @param cid The component identifier of the target component.
	 *  @return The corresponding service.
	 */
	public <T> IFuture<T> searchService(Class<T> type, IComponentIdentifier cid)
	{
		return SServiceProvider.getService(getComponent(), cid, type);
	}
	
	/**
	 *  Get a required service fetcher.
	 *  @param name The required service name.
	 *  @return The service fetcher.
	 */
	protected IRequiredServiceFetcher getRequiredServiceFetcher(String name)
	{
//		if(shutdowned)
//			throw new ComponentTerminatedException(id);

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
//		if(shutdowned)
//		{
//			throw new ComponentTerminatedException(id);
//		}

		throw new UnsupportedOperationException();
//		return new DefaultServiceFetcher(this, getComponent(), cinfo.isRealtime());
	}
	
//	/**
//	 *  Has the service a property provider.
//	 */
//	public boolean hasRequiredServicePropertyProvider(IServiceIdentifier sid)
//	{
//		return reqserprops!=null? reqserprops.get(sid)!=null: false;
//	}
	
	//-------- IServiceProvider interface --------
	
//	/**
//	 *  Get all services of a type.
//	 *  @param type The class.
//	 *  @return The corresponding services.
//	 */
//	public ITerminableIntermediateFuture<IService> getServices(ISearchManager manager, IVisitDecider decider, IResultSelector selector)
//	{
//		return new TerminableIntermediateFuture<IService>(new UnsupportedOperationException());
////		return manager.searchServices(this, decider, selector, services!=null ? services : Collections.EMPTY_MAP);
//	}
	
//	/**
//	 *  Get the parent service container.
//	 *  @return The parent container.
//	 */
//	public IFuture<IServiceProvider>	getParent()
//	{
//		final Future<IServiceProvider> ret = new Future<IServiceProvider>();
//		
//		if(component.getComponentIdentifier().getParent()!=null)
//		{
//			SServiceProvider.getServiceUpwards(this, IComponentManagementService.class)
//				.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IServiceProvider>(ret)
//			{
//				public void customResultAvailable(final IComponentManagementService cms)
//				{
//					cms.getExternalAccess(component.getComponentIdentifier().getParent())
//						.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, IServiceProvider>(ret)
//					{
//						public void customResultAvailable(IExternalAccess parent)
//						{
//							ret.setResult(parent.getServiceProvider());
//						}
//					});
//				}
//			});
//		}
//		else
//		{
//			ret.setResult(null);
//		}
//		
//		return ret;
//	}
	
//	/**
//	 *  Get the children container.
//	 *  @return The children container.
//	 */
//	public IFuture<Collection<IServiceProvider>>	getChildren()
//	{
//		final Future<Collection<IServiceProvider>> ret = new Future<Collection<IServiceProvider>>();
//
//		SServiceProvider.getServiceUpwards(this, IComponentManagementService.class)
//			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Collection<IServiceProvider>>(ret)
//		{
//			public void customResultAvailable(final IComponentManagementService cms)
//			{
//				cms.getChildren(component.getComponentIdentifier())
//					.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier[], Collection<IServiceProvider>>(ret)
//				{
//					public void customResultAvailable(IComponentIdentifier[] children)
//					{
//						if(children!=null)
//						{
//							final IResultListener<IServiceProvider> lis = new CollectionResultListener<IServiceProvider>(
//								children.length, true, new DelegationResultListener<Collection<IServiceProvider>>(ret));
//							for(int i=0; i<children.length; i++)
//							{
//								cms.getExternalAccess(children[i]).addResultListener(new IResultListener<IExternalAccess>()
//								{
//									public void resultAvailable(IExternalAccess exta)
//									{
//										try
//										{
//											lis.resultAvailable(exta.getServiceProvider());
//										}
//										catch(ComponentTerminatedException cte)
//										{
//											lis.exceptionOccurred(cte);
//										}
//									}
//									
//									public void exceptionOccurred(Exception exception)
//									{
//										lis.exceptionOccurred(exception);
//									}
//								});
//							}
//						}
//						else
//						{
//							List<IServiceProvider>	res	= Collections.emptyList();
//							ret.setResult(res);
//						}
//					}
//				});
//			}
//		});
//		
//		return ret;
//	}
	
	/**
	 *  Get the globally unique id of the provider.
	 *  @return The id of this provider.
	 */
	public IComponentIdentifier	getId()
	{
		return component.getComponentIdentifier();
	}
}
