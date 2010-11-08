package jadex.application.runtime.impl;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.IResultCommand;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.BasicService;
import jadex.commons.service.IInternalService;
import jadex.commons.service.IServiceIdentifier;
import jadex.commons.service.SServiceProvider;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class ServiceInvocationHandler implements InvocationHandler
{
	/** The map of own methods. */
	protected static Map ownmethods;
	
	static
	{
		ownmethods = new HashMap();
		try
		{
			ownmethods.put(IInternalService.class.getMethod("getServiceIdentifier", new Class[0]), new IResultCommand()
			{
				public Object execute(Object args)
				{
					return ((Object[])args)[5];
				}
			});
			
			// todo: implement methods?!
			ownmethods.put(IInternalService.class.getMethod("getPropertyMap", new Class[0]), new IResultCommand()
			{
				public Object execute(Object args)
				{
					return null;
				}
			});
			ownmethods.put(IInternalService.class.getMethod("signalStarted", new Class[0]), new IResultCommand()
			{
				public Object execute(Object args)
				{
					return new Future(null);
				}
			});
			ownmethods.put(IInternalService.class.getMethod("startService", new Class[0]), new IResultCommand()
			{
				public Object execute(Object args)
				{
					return new Future(null);
				}
			});
			ownmethods.put(IInternalService.class.getMethod("shutdownService", new Class[0]), new IResultCommand()
			{
				public Object execute(Object args)
				{
					return new Future(null);
				}
			});
			ownmethods.put(IInternalService.class.getMethod("isValid", new Class[0]), new IResultCommand()
			{
				public Object execute(Object args)
				{
					return true;
				}
			});
			
			ownmethods.put(Object.class.getMethod("toString", new Class[0]), new IResultCommand()
			{
				public Object execute(Object args)
				{
					Object proxy = ((Object[])args)[0];
					Object obj = Proxy.getInvocationHandler(proxy);
					return obj.toString();
				}
			});
			ownmethods.put(Object.class.getMethod("equals", new Class[]{Object.class}), new IResultCommand()
			{
				public Object execute(Object as)
				{
					Object proxy = ((Object[])as)[0];
					Object obj = Proxy.getInvocationHandler(proxy);
					Object[] args = (Object[])((Object[])as)[1];
					return new Boolean(args[0]!=null && Proxy.isProxyClass(args[0].getClass())
						&& obj.equals(Proxy.getInvocationHandler(args[0])));
				}
			});
			ownmethods.put(Object.class.getMethod("hashCode", new Class[0]), new IResultCommand()
			{
				public Object execute(Object args)
				{
					Object proxy = ((Object[])args)[0];
					Object obj = Proxy.getInvocationHandler(proxy);
					return new Integer(Proxy.getInvocationHandler(obj).hashCode());
				}
			});
			// todo: other object methods?!
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//-------- attributes --------
	
	/** The external access. */
	protected IApplicationExternalAccess ea;
	
	/** The component type. */
	protected String componenttype;
	
	/** The interface type. */
	protected Class servicetype;
	
	/** The service identifier. */
	protected IServiceIdentifier sid;
	
//	protected Future creating;
	
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public ServiceInvocationHandler(IApplicationExternalAccess ea, 
		String componenttype, final Class servicetype)
	{
		this.ea = ea;
		this.componenttype = componenttype;
		this.servicetype = servicetype;
		this.sid = BasicService.createServiceIdentifier(ea.getServiceProvider().getId(), servicetype, getClass());
	}
	
	//-------- methods --------
	
	/**
	 *  Invoke a method.
	 */
	public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable
	{
		Object ret;
		
		IResultCommand resc = (IResultCommand)ownmethods.get(method);
		if(resc!=null)
		{
			ret = resc.execute(new Object[]{proxy, args, ea, componenttype, servicetype, sid});
		}
		else
		{
			final Future fut = new Future(); 
			ret = fut;
			
//			System.out.println("Invoked: "+method.getName());
			
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
								invokeServiceMethod(cms, cid, method, args, fut);
							}
							else
							{
								final IResultListener lis = new DelegationResultListener(fut)
								{
									public void customResultAvailable(Object source, Object result)
									{
										IComponentIdentifier cid = (IComponentIdentifier)result;
										invokeServiceMethod(cms, cid, method, args, fut);
										
//										synchronized(ServiceInvocationHandler.this)
//										{
//											creating = null;
//										}
									}
								};
								
//								boolean addlis = false;
//								synchronized(ServiceInvocationHandler.this)
//								{
//									if(creating!=null)
//									{
//										addlis = true;
//									}
//								}
								
//								if(addlis)
//								{
//									creating.addResultListener(lis);
//								}
//								else
//								{
									ea.getFileName(componenttype).addResultListener(new DelegationResultListener(fut)
									{
										public void customResultAvailable(Object source, Object result)
										{
											String filename = (String)result;
											
											cms.createComponent(null, filename, new CreationInfo(
												ea.getComponentIdentifier()), null).addResultListener(lis);
										}
									});
//								}
							}
						}
					});
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Invoke the service method.
	 */
	protected void invokeServiceMethod(final IComponentManagementService cms, final IComponentIdentifier cid,
		final Method method, final Object[] args, final Future ret)
	{
		cms.getExternalAccess(cid).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object source, Object result)
			{
				IExternalAccess cea = (IExternalAccess)result;
				SServiceProvider.getService(cea.getServiceProvider(), servicetype)
					.addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object source, Object result)
					{
						if(result!=null)
						{
							try
							{
								Object res = method.invoke(result, args);
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
}
