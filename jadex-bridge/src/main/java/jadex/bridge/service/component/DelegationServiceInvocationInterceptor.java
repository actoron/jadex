package jadex.bridge.service.component;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IRequiredServiceFetcher;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * 
 */
public class DelegationServiceInvocationInterceptor implements IServiceInvocationInterceptor
{
	//-------- attributes --------
	
	/** The external access. */
	protected IExternalAccess ea;
		
	/** The service info. */
	protected RequiredServiceInfo info;
	
	/** The service binding. */
	protected RequiredServiceBinding binding;
	
	/** The service fetcher. */
	protected IRequiredServiceFetcher fetcher;

	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public DelegationServiceInvocationInterceptor(IExternalAccess ea, RequiredServiceInfo info, RequiredServiceBinding binding)
	{
		this.ea = ea;
		this.info = info;
		this.binding = binding;
		this.fetcher = new DefaultServiceFetcher();
	}
	
	//-------- methods --------
	
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public void execute(final ServiceInvocationContext sic) 	
	{
		final Future ret = new Future(); 
//		System.out.println("Invoked: "+method.getName());
		
		ea.scheduleStep(new IComponentStep()
		{
//			@XMLClassname("invoc")
			public Object execute(final IInternalAccess ia)
			{
				fetcher.getService(info, binding, ia.getServiceProvider(), false)
					.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result) 
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
				}));
				return null;
			}
		});
		
		sic.setResult(ret);
	}

	/**
	 *  Get the ea.
	 *  @return the ea.
	 */
	public IExternalAccess getExternalAccess()
	{
		return ea;
	}

	/**
	 *  Get the standard interceptors for composite service proxies.
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
					context.setResult(IFuture.DONE);
				}
			});
			ret.put(IInternalService.class.getMethod("startService", new Class[0]), new IServiceInvocationInterceptor()
			{
				public void execute(ServiceInvocationContext context)
				{
					context.setResult(IFuture.DONE);
				}
			});
			ret.put(IInternalService.class.getMethod("shutdownService", new Class[0]), new IServiceInvocationInterceptor()
			{
				public void execute(ServiceInvocationContext context)
				{
					context.setResult(IFuture.DONE);
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
	public static IInternalService createServiceProxy(IExternalAccess ea, RequiredServiceInfo info, RequiredServiceBinding binding, ClassLoader classloader)
	{
		return (IInternalService)Proxy.newProxyInstance(classloader, new Class[]{IInternalService.class, info.getType()}, 
			new BasicServiceInvocationHandler(BasicService.createServiceIdentifier(ea.getServiceProvider().getId(), info.getType(), BasicServiceInvocationHandler.class), 
			DelegationServiceInvocationInterceptor.getInterceptors(), 
			new DelegationServiceInvocationInterceptor(ea, info, binding)));
	}
}
