package jadex.bridge.service.component;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.BasicServiceContainer;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IRequiredServiceFetcher;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;

import java.util.Collections;
import java.util.logging.Logger;

/**
 *  Service container for active components.
 */
public class ComponentServiceContainer	extends BasicServiceContainer
{
	//-------- attributes --------
	
	/** The component adapter. */
	protected IComponentAdapter adapter;
	
	/** The external access. */
	protected IComponentInstance instance;
	
	/** The cms. */
	protected IComponentManagementService cms;
	
	/** The component type. */
	protected String type;
	
	/** The parameter copy flag. */
	protected boolean copy;
	
	//-------- constructors --------

	/**
	 *  Create a new service container.
	 */
//	public ComponentServiceContainer(IExternalAccess ea, IComponentAdapter adapter, String type)
	public ComponentServiceContainer(IComponentAdapter adapter, String type, boolean copy, IComponentInstance instance)
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
	public IFuture getRequiredService(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind)
	{
		return new ComponentFuture(instance.getExternalAccess(), adapter, super.getRequiredService(info, binding, rebind), false);
	}
	
	/**
	 *  Get required services.
	 *  @return The services.
	 */
	public IIntermediateFuture getRequiredServices(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind)
	{
		return new ComponentIntermediateFuture(instance.getExternalAccess(), adapter, super.getRequiredServices(info, binding, rebind), false);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public IFuture searchService(Class type)
	{
		final Future	fut	= new Future();
		SServiceProvider.getService(this, type).addResultListener(new DelegationResultListener(fut)
		{
			public void customResultAvailable(Object result)
			{
				fut.setResult(BasicServiceInvocationHandler.createRequiredServiceProxy(null, instance.getExternalAccess(), adapter, (IInternalService)result, null, null, null, copy));
			}
		});
		return new ComponentFuture(instance.getExternalAccess(), adapter, fut, false);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public IFuture searchService(Class type, String scope)
	{
		final Future	fut	= new Future();
		SServiceProvider.getService(this, type, scope).addResultListener(new DelegationResultListener(fut)
		{
			public void customResultAvailable(Object result)
			{
				fut.setResult(BasicServiceInvocationHandler.createRequiredServiceProxy(null, instance.getExternalAccess(), adapter, (IInternalService)result, null, null, null, copy));
			}
		});
		return new ComponentFuture(instance.getExternalAccess(), adapter, fut, false);
	}
	
	// todo: remove
	/**
	 *  Get one service of a type and only search upwards (parents).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public IFuture searchServiceUpwards(Class type)
	{
		final Future	fut	= new Future();
		SServiceProvider.getServiceUpwards(this, type).addResultListener(new DelegationResultListener(fut)
		{
			public void customResultAvailable(Object result)
			{
				fut.setResult(BasicServiceInvocationHandler.createRequiredServiceProxy(null, instance.getExternalAccess(), adapter, (IInternalService)result, null, null, null, copy));
			}
		});
		return new ComponentFuture(instance.getExternalAccess(), adapter, fut, false);
	}

	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IIntermediateFuture searchServices(Class type)
	{
		final IntermediateFuture	fut	= new IntermediateFuture();
		SServiceProvider.getServices(this, type).addResultListener(new IntermediateDelegationResultListener(fut)
		{
			public void customIntermediateResultAvailable(Object result)
			{
				fut.addIntermediateResult(BasicServiceInvocationHandler.createRequiredServiceProxy(null, instance.getExternalAccess(), adapter, (IInternalService)result, null, null, null, copy));
			}
		});
		return new ComponentIntermediateFuture(instance.getExternalAccess(), adapter, fut, false);
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IIntermediateFuture searchServices(Class type, String scope)
	{
		final IntermediateFuture	fut	= new IntermediateFuture();
		SServiceProvider.getServices(this, type, scope).addResultListener(new IntermediateDelegationResultListener(fut)
		{
			public void customIntermediateResultAvailable(Object result)
			{
				fut.addIntermediateResult(BasicServiceInvocationHandler.createRequiredServiceProxy(null, instance.getExternalAccess(), adapter, (IInternalService)result, null, null, null, copy));
			}
		});
		return new ComponentIntermediateFuture(instance.getExternalAccess(), adapter, fut, false);
	}
	
	/**
	 *  Get the parent service container.
	 *  @return The parent container.
	 */
	public IFuture	getParent()
	{
		final Future ret = new Future();
		
		ret.setResult(adapter.getParent()!=null ? adapter.getParent().getServiceProvider() : null);
		
		return ret;
	}
	
	/**
	 *  Get the children service containers.
	 *  @return The children containers.
	 */
	public IFuture	getChildren()
	{
		final Future ret = new Future();
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
	public IFuture start()
	{
		final Future ret = new Future();
		
//		System.out.println("search clock: "+getId());
		SServiceProvider.getServiceUpwards(ComponentServiceContainer.this, IComponentManagementService.class).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				cms = (IComponentManagementService)result;
//				System.out.println("Has cms: "+getId()+" "+cms);

				// Services may need other services and thus need to be able to search
				// the container.
				ComponentServiceContainer.super.start().addResultListener(new DelegationResultListener(ret));
			}
		});
		
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
