package jadex.application.runtime.impl;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.bridge.BasicServiceInvocationHandler;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IServiceInvocationInterceptor;
import jadex.bridge.ServiceInvocationContext;
import jadex.commons.Future;
import jadex.commons.IFuture;
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
	
	/** The static component id (if any). */
	protected IComponentIdentifier cid;
	
	/** A future of a component currently being created (if any). */
	protected Future creating;
	
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public CompositeServiceInvocationInterceptor(IApplicationExternalAccess ea, 
		String componenttype, Class servicetype, IComponentIdentifier cid)
	{
		this.ea = ea;
		this.componenttype = componenttype;
		this.cid = cid;
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
		
		ea.scheduleStep(new IComponentStep()
		{
			public static final String XML_CLASSNAME = "invoc"; 
			public Object execute(final IInternalAccess ia)
			{
				// A concrete component has been specified.
				if(cid!=null)
				{
					invokeServiceMethod(ia, cid, sic, fut);
				}
				else
				{
					ea.getChildren(componenttype).addResultListener(ia.createResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							final Collection res = (Collection)result;
							
							if(res!=null && res.size()>0)
							{
								final IComponentIdentifier cid = (IComponentIdentifier)res.iterator().next();
								invokeServiceMethod(ia, cid, sic, fut);
							}
							else
							{
								IResultListener	lis	= ia.createResultListener(new DelegationResultListener(fut)
								{
									public void customResultAvailable(Object source, Object result)
									{
										IComponentIdentifier cid = (IComponentIdentifier)result;
										invokeServiceMethod(ia, cid, sic, fut);
									}
								});
								
								if(creating!=null)
								{									
									creating.addResultListener(lis);
								}
								else
								{
									creating	= new Future();
									creating.addResultListener(lis);
									SServiceProvider.getService(ea.getServiceProvider(), IComponentManagementService.class)
										.addResultListener(ia.createResultListener(new DelegationResultListener(fut)
									{
										public void customResultAvailable(Object source, Object result)
										{
											final IComponentManagementService cms = (IComponentManagementService)result;
											ea.getFileName(componenttype).addResultListener(ia.createResultListener(new DelegationResultListener(fut)
											{
												public void customResultAvailable(Object source, Object result)
												{
													String filename = (String)result;
													cms.createComponent(null, filename, new CreationInfo(ea.getComponentIdentifier()), null)
														.addResultListener(ia.createResultListener(new DelegationResultListener(fut)
													{
														public void customResultAvailable(Object source, Object result)
														{
															creating.setResult(result);
															creating	= null;
														}
													}));
												}
											}));
										}
									}));									
								}
							}
						}
					}));
				}
				return null;
			}
		});
		
		sic.setResult(ret);
	}

	/**
	 *  Invoke the service method.
	 */
	protected void invokeServiceMethod(final IInternalAccess ia, final IComponentIdentifier cid,
		final ServiceInvocationContext sic, final Future ret)
	{
		SServiceProvider.getService(ea.getServiceProvider(), IComponentManagementService.class)
			.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object source, Object result)
			{
				final IComponentManagementService cms = (IComponentManagementService)result;
		
				cms.getExternalAccess(cid).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object source, Object result)
					{
						final BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)Proxy.getInvocationHandler(sic.getProxy());
		
						IExternalAccess cea = (IExternalAccess)result;
						SServiceProvider.getDeclaredService(cea.getServiceProvider(), handler.getServiceIdentifier().getServiceType())
							.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
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
											((IFuture)res).addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
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
								else
								{
									ret.setException(new RuntimeException("No service found: "+ia.getComponentIdentifier()+" "+handler.getServiceIdentifier().getServiceType()));
								}
							}
						}));
					}
				}));
			}
		}));
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
			ret.put(IInternalService.class.getMethod("getServiceIdentifier", new Class[0]), new IServiceInvocationInterceptor()
			{
				public void execute(ServiceInvocationContext context)
				{
					Object proxy = context.getProxy();
					BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)Proxy.getInvocationHandler(proxy);
					context.setResult(handler.getServiceIdentifier());
				}
			});
			
			// todo: implement methods?!
			ret.put(IInternalService.class.getMethod("getPropertyMap", new Class[0]), new IServiceInvocationInterceptor()
			{
				public void execute(ServiceInvocationContext context)
				{
				}
			});
			ret.put(IInternalService.class.getMethod("signalStarted", new Class[0]), new IServiceInvocationInterceptor()
			{
				public void execute(ServiceInvocationContext context)
				{
					context.setResult(new Future(null));
				}
			});
			ret.put(IInternalService.class.getMethod("startService", new Class[0]), new IServiceInvocationInterceptor()
			{
				public void execute(ServiceInvocationContext context)
				{
					context.setResult(new Future(null));
				}
			});
			ret.put(IInternalService.class.getMethod("shutdownService", new Class[0]), new IServiceInvocationInterceptor()
			{
				public void execute(ServiceInvocationContext context)
				{
					context.setResult(new Future(null));
				}
			});
			ret.put(IInternalService.class.getMethod("isValid", new Class[0]), new IServiceInvocationInterceptor()
			{
				public void execute(ServiceInvocationContext context)
				{
					context.setResult(true);
				}
			});
			
			ret.put(Object.class.getMethod("toString", new Class[0]), new IServiceInvocationInterceptor()
			{
				public void execute(ServiceInvocationContext context)
				{
					Object proxy = context.getProxy();
					InvocationHandler handler = (InvocationHandler)Proxy.getInvocationHandler(proxy);
					context.setResult(handler.toString());
				}
			});
			ret.put(Object.class.getMethod("equals", new Class[]{Object.class}), new IServiceInvocationInterceptor()
			{
				public void execute(ServiceInvocationContext context)
				{
					Object proxy = context.getProxy();
					InvocationHandler handler = (InvocationHandler)Proxy.getInvocationHandler(proxy);
					Object[] args = (Object[])context.getArguments();
					context.setResult(new Boolean(args[0]!=null && Proxy.isProxyClass(args[0].getClass())
						&& handler.equals(Proxy.getInvocationHandler(args[0]))));
				}
			});
			ret.put(Object.class.getMethod("hashCode", new Class[0]), new IServiceInvocationInterceptor()
			{
				public void execute(ServiceInvocationContext context)
				{
					Object proxy = context.getProxy();
					InvocationHandler handler = Proxy.getInvocationHandler(proxy);
					context.setResult(new Integer(handler.hashCode()));
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
	public static IInternalService createServiceProxy(Class servicetype, String componenttype, IApplicationExternalAccess ea, ClassLoader classloader, IComponentIdentifier cid)
	{
		return (IInternalService)Proxy.newProxyInstance(classloader, new Class[]{IInternalService.class, servicetype}, 
			new BasicServiceInvocationHandler(BasicService.createServiceIdentifier(ea.getServiceProvider().getId(), servicetype, BasicServiceInvocationHandler.class), 
			CompositeServiceInvocationInterceptor.getInterceptors(), 
			new CompositeServiceInvocationInterceptor(ea, componenttype, servicetype, cid)));
//			new CompositeServiceInvocationInterceptor((IApplicationExternalAccess)getExternalAccess(), ct.getName(), servicetype));
	}
}
