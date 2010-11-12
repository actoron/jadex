package jadex.application.runtime.impl;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.bridge.BasicServiceInvocationHandler;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IServiceInvocationInterceptor;
import jadex.bridge.ServiceInvocationContext;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.IResultCommand;
import jadex.commons.collection.MultiCollection;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.BasicService;
import jadex.commons.service.IInternalService;
import jadex.commons.service.SServiceProvider;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Collection;

/**
 *  The composite (or application) service invocation handler is responsible for
 *  service calls on composite services. These service calls are
 *  directed towards a service implementation of a contained 
 *  component type. It has to lookup or search for a fitting
 */
public class CompositeServiceInvocationInterceptor implements IServiceInvocationInterceptor
{
	//-------- attributes --------
	
	/** The external access. */
	protected IApplicationExternalAccess ea;
	
	/** The component type. */
	protected String componenttype;
	
//	protected Future creating;
	
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public CompositeServiceInvocationInterceptor(IApplicationExternalAccess ea, String componenttype, final Class servicetype)
	{
		this.ea = ea;
		this.componenttype = componenttype;
	}
	
	//-------- methods --------
	
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public void execute(final ServiceInvocationContext sic) 	
	{
		final Future fut = new Future(); 
		Object ret = fut;
//		System.out.println("Invoked: "+method.getName());
	
		// todo: schedluleStep?! -> createResultListener
		ea.getChildren(componenttype).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final Collection res = (Collection)result;
				
				SServiceProvider.getService(ea.getServiceProvider(), IComponentManagementService.class)
					.addResultListener(new DelegationResultListener(fut)
				{
					public void customResultAvailable(Object source, Object result)
					{
						final IComponentManagementService cms = (IComponentManagementService)result;
				
						if(res!=null && res.size()>0)
						{
							final IComponentIdentifier cid = (IComponentIdentifier)res.iterator().next();
							invokeServiceMethod(cms, cid, sic, fut);
						}
						else
						{
							final IResultListener lis = new DelegationResultListener(fut)
							{
								public void customResultAvailable(Object source, Object result)
								{
									IComponentIdentifier cid = (IComponentIdentifier)result;
									invokeServiceMethod(cms, cid, sic, fut);
									
//									synchronized(ServiceInvocationHandler.this)
//									{
//										creating = null;
//									}
								}
							};
							
//							boolean addlis = false;
//							synchronized(ServiceInvocationHandler.this)
//							{
//								if(creating!=null)
//								{
//									addlis = true;
//								}
//							}
					
//							if(addlis)
//							{
//								creating.addResultListener(lis);
//							}
//							else
//							{
								ea.getFileName(componenttype).addResultListener(new DelegationResultListener(fut)
								{
									public void customResultAvailable(Object source, Object result)
									{
										String filename = (String)result;
										
										cms.createComponent(null, filename, new CreationInfo(
											ea.getComponentIdentifier()), null).addResultListener(lis);
									}
								});
//							}
						}
					}
				});
			}
		});
		
		sic.setResult(ret);
	}

	/**
	 *  Invoke the service method.
	 */
	protected void invokeServiceMethod(final IComponentManagementService cms, final IComponentIdentifier cid,
		final ServiceInvocationContext sic, final Future ret)
	{
		cms.getExternalAccess(cid).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object source, Object result)
			{
				final BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)Proxy.getInvocationHandler(sic.getProxy());

				IExternalAccess cea = (IExternalAccess)result;
				SServiceProvider.getService(cea.getServiceProvider(), handler.getServiceIdentifier().getServiceType())
					.addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object source, Object result)
					{
						if(result!=null)
						{
							try
							{
								Object res = sic.getMethod().invoke(result, sic.getArguments());
								if(res instanceof IFuture)
								{
									((IFuture)res).addResultListener(new DelegationResultListener(ret));
								}
								else
								{
									ret.setResult(res);
								}
							}
							catch(Exception e)
							{
								ret.setException(e);
							}
						}
					}
				});
			}
		});
	}
	
	/**
	 *  Get the ea.
	 *  @return the ea.
	 */
	public IApplicationExternalAccess getExternalAccess()
	{
		return ea;
	}

	/**
	 *  Get the componenttype.
	 *  @return the componenttype.
	 */
	public String getComponentType()
	{
		return componenttype;
	}
	
	/**
	 *  Get the standard interceptors for composite service proxies;
	 */
	public static MultiCollection getInterceptors()
	{
		MultiCollection ret = new MultiCollection();
		try
		{
			ret.put(IInternalService.class.getMethod("getServiceIdentifier", new Class[0]), new IResultCommand()
			{
				public Object execute(Object args)
				{
					Object proxy = ((ServiceInvocationContext)args).getProxy();
					BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)Proxy.getInvocationHandler(proxy);
					return handler.getServiceIdentifier();
				}
			});
			
			// todo: implement methods?!
			ret.put(IInternalService.class.getMethod("getPropertyMap", new Class[0]), new IResultCommand()
			{
				public Object execute(Object args)
				{
					return null;
				}
			});
			ret.put(IInternalService.class.getMethod("signalStarted", new Class[0]), new IResultCommand()
			{
				public Object execute(Object args)
				{
					return new Future(null);
				}
			});
			ret.put(IInternalService.class.getMethod("startService", new Class[0]), new IResultCommand()
			{
				public Object execute(Object args)
				{
					return new Future(null);
				}
			});
			ret.put(IInternalService.class.getMethod("shutdownService", new Class[0]), new IResultCommand()
			{
				public Object execute(Object args)
				{
					return new Future(null);
				}
			});
			ret.put(IInternalService.class.getMethod("isValid", new Class[0]), new IResultCommand()
			{
				public Object execute(Object args)
				{
					return true;
				}
			});
			
			ret.put(Object.class.getMethod("toString", new Class[0]), new IResultCommand()
			{
				public Object execute(Object args)
				{
					Object proxy = ((ServiceInvocationContext)args).getProxy();
					InvocationHandler handler = (InvocationHandler)Proxy.getInvocationHandler(proxy);
					return handler.toString();
				}
			});
			ret.put(Object.class.getMethod("equals", new Class[]{Object.class}), new IResultCommand()
			{
				public Object execute(Object as)
				{
					Object proxy = ((ServiceInvocationContext)as).getProxy();
					InvocationHandler handler = (InvocationHandler)Proxy.getInvocationHandler(proxy);
					Object[] args = (Object[])((ServiceInvocationContext)as).getArguments();
					return new Boolean(args[0]!=null && Proxy.isProxyClass(args[0].getClass())
						&& handler.equals(Proxy.getInvocationHandler(args[0])));
				}
			});
			ret.put(Object.class.getMethod("hashCode", new Class[0]), new IResultCommand()
			{
				public Object execute(Object args)
				{
					Object proxy = ((ServiceInvocationContext)args).getProxy();
					InvocationHandler handler = Proxy.getInvocationHandler(proxy);
					return new Integer(Proxy.getInvocationHandler(handler).hashCode());
				}
			});
			// todo: other object methods?!
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 *  Create a new composite (application) service proxy.
	 */
	public static IInternalService createServiceProxy(Class servicetype, String componenttype, IApplicationExternalAccess ea, ClassLoader classloader)
	{
		return (IInternalService)Proxy.newProxyInstance(classloader, new Class[]{IInternalService.class, servicetype}, 
			new BasicServiceInvocationHandler(BasicService.createServiceIdentifier(ea.getServiceProvider().getId(), servicetype, BasicServiceInvocationHandler.class), 
			CompositeServiceInvocationInterceptor.getInterceptors(), 
			new CompositeServiceInvocationInterceptor(ea, componenttype, servicetype)));
//			new CompositeServiceInvocationInterceptor((IApplicationExternalAccess)getExternalAccess(), ct.getName(), servicetype));
	}
}
