package jadex.bridge.service.component;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.BasicServiceContainer;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IRequiredServiceFetcher;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.marshal.IMarshalService;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Logger;

/* $if !android $ */
import javax.management.ServiceNotFoundException;
/* $else $
import javaxx.management.ServiceNotFoundException;
$endif $ */

/**
 *  Service container for active components.
 */
public class ComponentServiceContainer	extends BasicServiceContainer
{
	//-------- attributes --------
	
	/** The component adapter. */
	protected IComponentAdapter adapter;
	
	/** The internal access. */
	protected IInternalAccess instance;
	
	/** The cms. */
	protected IComponentManagementService cms;

	/** The marshal service. */
	protected IMarshalService marshal;
	
	/** The component type. */
	protected String type;
	
	/** The parameter copy flag. */
	protected boolean copy;
	
	//-------- constructors --------

	/**
	 *  Create a new service container.
	 */
//	public ComponentServiceContainer(IExternalAccess ea, IComponentAdapter adapter, String type)
	public ComponentServiceContainer(IComponentAdapter adapter, String type, boolean copy, IInternalAccess instance)//IComponentInstance instance)
	{
		super(adapter.getComponentIdentifier());
		this.adapter = adapter;
		this.type	= type;
		this.copy = copy;
		this.instance = instance;
	}
	
	//-------- interface methods --------
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public IFuture<IService> getRequiredService(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind)
	{
		return new ComponentFuture<IService>(instance.getExternalAccess(), adapter, 
			super.getRequiredService(info, binding, rebind), false, marshal);
	}
	
	/**
	 *  Get required services.
	 *  @return The services.
	 */
	public IIntermediateFuture<IService> getRequiredServices(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind)
	{
		return new ComponentIntermediateFuture<IService>(instance.getExternalAccess(), adapter, 
			super.getRequiredServices(info, binding, rebind), false, marshal);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public <T> IFuture<T> searchService(Class<T> type)
	{
		final Future<T>	fut	= new Future<T>();
		SServiceProvider.getService(this, type).addResultListener(new DelegationResultListener(fut)
		{
			public void customResultAvailable(Object result)
			{
				fut.setResult((T)BasicServiceInvocationHandler.createRequiredServiceProxy(instance, 
					instance.getExternalAccess(), adapter, (IService)result, null, null, null, copy));
			}
		});
		return new ComponentFuture<T>(instance.getExternalAccess(), adapter, fut, false, marshal);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public <T> IFuture<T> searchService(Class<T> type, String scope)
	{
		final Future<T>	fut	= new Future<T>();
		SServiceProvider.getService(this, type, scope).addResultListener(new DelegationResultListener(fut)
		{
			public void customResultAvailable(Object result)
			{
				fut.setResult((T)BasicServiceInvocationHandler.createRequiredServiceProxy(instance, 
					instance.getExternalAccess(), adapter, (IService)result, null, null, null, copy));
			}
		});
		return new ComponentFuture<T>(instance.getExternalAccess(), adapter, fut, false, marshal);
	}
	
	// todo: remove
	/**
	 *  Get one service of a type and only search upwards (parents).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public <T> IFuture<T> searchServiceUpwards(Class<T> type)
	{
		final Future<T>	fut	= new Future<T>();
		SServiceProvider.getServiceUpwards(this, type).addResultListener(new DelegationResultListener(fut)
		{
			public void customResultAvailable(Object result)
			{
				fut.setResult((T)BasicServiceInvocationHandler.createRequiredServiceProxy(instance, 
					instance.getExternalAccess(), adapter, (IService)result, null, null, null, copy));
			}
		});
		return new ComponentFuture<T>(instance.getExternalAccess(), adapter, fut, false, marshal);
	}

	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public <T> IIntermediateFuture<T> searchServices(Class<T> type)
	{
		final IntermediateFuture<T>	fut	= new IntermediateFuture<T>();
		SServiceProvider.getServices(this, type).addResultListener(new IntermediateDelegationResultListener(fut)
		{
			public void customIntermediateResultAvailable(Object result)
			{
				fut.addIntermediateResult((T)BasicServiceInvocationHandler.createRequiredServiceProxy(instance, instance.getExternalAccess(), 
					adapter, (IService)result, null, null, null, copy));
			}
		});
		return new ComponentIntermediateFuture<T>(instance.getExternalAccess(), adapter, fut, false, marshal);
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public <T> IIntermediateFuture<T> searchServices(Class<T> type, String scope)
	{
		final IntermediateFuture<T>	fut	= new IntermediateFuture<T>();
		SServiceProvider.getServices(this, type, scope).addResultListener(new IntermediateDelegationResultListener(fut)
		{
			public void customIntermediateResultAvailable(Object result)
			{
				fut.addIntermediateResult((T)BasicServiceInvocationHandler.createRequiredServiceProxy(instance, instance.getExternalAccess(), 
					adapter, (IService)result, null, null, null, copy));
			}
		});
		return new ComponentIntermediateFuture<T>(instance.getExternalAccess(), adapter, fut, false, marshal);
	}
	
	/**
	 *  Get the parent service container.
	 *  @return The parent container.
	 */
	public IFuture<IServiceProvider>	getParent()
	{
		final Future<IServiceProvider> ret = new Future<IServiceProvider>();
		
		ret.setResult(adapter.getParent()!=null ? adapter.getParent().getServiceProvider() : null);
		
		return ret;
	}
	
	/**
	 *  Get the children service containers.
	 *  @return The children containers.
	 */
	public IFuture<Collection<IServiceProvider>> getChildren()
	{
		final Future<Collection<IServiceProvider>> ret = new Future<Collection<IServiceProvider>>();
//		ComponentFuture ret = new ComponentFuture(ea, adapter, oldret);
		
		adapter.getChildrenIdentifiers().addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				if(result!=null)
				{
					IComponentIdentifier[] childs = (IComponentIdentifier[])result;
//					System.out.println("childs: "+adapter.getComponentIdentifier()+" "+SUtil.arrayToString(childs));
					final IResultListener lis = new CollectionResultListener(
						childs.length, true, new DelegationResultListener(ret));
					for(int i=0; i<childs.length; i++)
					{
						cms.getExternalAccess(childs[i]).addResultListener(new IResultListener()
						{
							public void resultAvailable(Object result)
							{
								IExternalAccess exta = (IExternalAccess)result;
								lis.resultAvailable(exta.getServiceProvider());
							}
							
							public void exceptionOccurred(Exception exception)
							{
								lis.exceptionOccurred(exception);
							}
						});
					}
				}
				else
				{
					ret.setResult(Collections.EMPTY_LIST);
				}
			}
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
//	/**
//	 *  Get the external access.
//	 */
//	public IFuture getExternalAccess()
//	{
//		final Future ret = new Future();
//		if(exta==null)
//		{
//			cms.getExternalAccess(adapter.getComponentIdentifier())
//				.addResultListener(new DelegationResultListener(ret)
//			{
//				public void customResultAvailable(Object result)
//				{
//	//				System.out.println("exta: "+result);
//					exta = (IExternalAccess)result;
//					ret.setResult(exta);
//				}
//			});
//		}
//		else
//		{
//			ret.setResult(exta);
//		}
//		return ret;
//	}
	
	/**
	 *  Create a service fetcher.
	 */
	public IRequiredServiceFetcher createServiceFetcher(String name)
	{
		return new DefaultServiceFetcher(this, copy);
	}
	
	/**
	 *  Get the type of the service provider (e.g. enclosing component type).
	 *  @return The type of this provider.
	 */
	public String	getType()
	{
		return type;
	}	
	
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture<Void> start()
	{
		final Future<Void> ret = new Future<Void>();
		
//		System.out.println("search clock: "+getId());
		SServiceProvider.getServiceUpwards(ComponentServiceContainer.this, IComponentManagementService.class)
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
		{
			public void customResultAvailable(IComponentManagementService result)
			{
				cms = result;
//				System.out.println("Has cms: "+getId()+" "+cms);
				
				SServiceProvider.getServiceUpwards(ComponentServiceContainer.this, IMarshalService.class)
					.addResultListener(new ExceptionDelegationResultListener<IMarshalService, Void>(ret)
				{
					public void customResultAvailable(IMarshalService result)
					{
						marshal = result;
						// Services may need other services and thus need to be able to search
						// the container.
						ComponentServiceContainer.super.start().addResultListener(new DelegationResultListener<Void>(ret));
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Shutdown the container.
	 */
	public IFuture<Void> shutdown()
	{
		Future<Void>	ret	= new Future<Void>();
		super.shutdown().addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				adapter	= null;
				cms	= null;
				instance	= null;
				marshal	= null;
				super.customResultAvailable(result);
			}
		});
		return ret;
	}
	
	/**
	 *  Called after a service has been started.
	 */
	public IFuture<Void> serviceStarted(final IInternalService service)
	{
		final Future<Void> ret = new Future<Void>();
		ProvidedServiceInfo info = getProvidedServiceInfo(service.getServiceIdentifier());
		final PublishInfo pi = info==null? null: info.getPublish();
		if(pi!=null)
		{
			getPublishService(instance, pi.getPublishType(), (Iterator<IPublishService>)null)
				.addResultListener(instance.createResultListener(new ExceptionDelegationResultListener<IPublishService, Void>(ret)
			{
				public void customResultAvailable(IPublishService ps)
				{
					ps.publishService(instance.getClassLoader(), service, pi)
						.addResultListener(instance.createResultListener(new DelegationResultListener<Void>(ret)));
				}
			}));
		}
		else
		{
			ret.setResult(null);
		}
		return ret;
	}
	
	/**
	 *  Called after a service has been shutdowned.
	 */
	public IFuture<Void> serviceShutdowned(IInternalService service)
	{
		final Future<Void> ret = new Future<Void>();
		final IServiceIdentifier sid = service.getServiceIdentifier();
		getPublishService(instance, type, null).addResultListener(instance.createResultListener(new IResultListener<IPublishService>()
		{
			public void resultAvailable(IPublishService ps)
			{
				ps.unpublishService(sid).addResultListener(new DelegationResultListener<Void>(ret));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// ignore, if no publish info
				ret.setResult(null);
				// todo: what if publish info but no publish service?
			}
		}));
		return ret;
	}
	
	/**
	 *  Get the publish service for a publish type (e.g. web service).
	 *  @param type The type.
	 *  @param services The iterator of publish services (can be null).
	 *  @return The publish service.
	 */
	public static IFuture<IPublishService> getPublishService(final IInternalAccess instance, final String type, final Iterator<IPublishService> services)
	{
		final Future<IPublishService> ret = new Future<IPublishService>();
		
		if(services==null)
		{
			IFuture<Collection<IPublishService>> fut = SServiceProvider.getServices(instance.getServiceContainer(), IPublishService.class, RequiredServiceInfo.SCOPE_PLATFORM);
			fut.addResultListener(instance.createResultListener(new ExceptionDelegationResultListener<Collection<IPublishService>, IPublishService>(ret)
			{
				public void customResultAvailable(Collection<IPublishService> result)
				{
					getPublishService(instance, type, result.iterator()).addResultListener(new DelegationResultListener<IPublishService>(ret));
				}
			}));
		}
		else
		{
			if(services.hasNext())
			{
				final IPublishService ps = (IPublishService)services.next();
				ps.isSupported(type).addResultListener(instance.createResultListener(new ExceptionDelegationResultListener<Boolean, IPublishService>(ret)
				{
					public void customResultAvailable(Boolean supported)
					{
						if(supported.booleanValue())
						{
							ret.setResult(ps);
						}
						else
						{
							getPublishService(instance, type, services).addResultListener(new DelegationResultListener<IPublishService>(ret));
						}
					}
				}));
			}
			else
			{
				ret.setException(new ServiceNotFoundException("IPublishService"));
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "ComponentServiceContainer(name="+getId()+")";
	}
	
	/**
	 *  Get the logger.
	 */
	protected Logger getLogger()
	{
		return adapter.getLogger();
	}
}
