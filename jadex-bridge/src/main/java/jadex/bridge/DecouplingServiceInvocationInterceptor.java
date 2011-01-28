package jadex.bridge;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.ThreadSuspendable;
import jadex.commons.collection.MultiCollection;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.service.IInternalService;
import jadex.commons.service.IServiceIdentifier;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 *  Invocation interceptor for executing a call on 
 *  the underlying component thread. 
 */
public class DecouplingServiceInvocationInterceptor implements IServiceInvocationInterceptor
{
	//-------- attributes --------
	
	/** The external access. */
	protected IExternalAccess ea;	
		
	/** The component adapter. */
	protected IComponentAdapter adapter;
	
	/** The service. */
	protected Object service;
	
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public DecouplingServiceInvocationInterceptor(IExternalAccess ea, IComponentAdapter adapter, Object service)
	{
		this.ea = ea;
		this.adapter = adapter;
		this.service = service;
	}
	
	//-------- methods --------
	
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public void execute(final ServiceInvocationContext sic) 	
	{
		Object ret;
		Class returntype = sic.getMethod().getReturnType();
		boolean scheduleable = returntype.equals(IFuture.class) || returntype.equals(void.class);
		boolean directcall = true;
		
		if(!adapter.isExternalThread() || (!scheduleable && directcall))
		{
			try
			{
				ret = sic.getMethod().invoke(service, sic.getArguments());
			}
			catch(Exception e)
			{
				if(returntype.equals(IFuture.class))
				{
					Future fut = new Future();
					fut.setException(e);
					ret = fut;
				}
				else
				{
					e.printStackTrace();
					throw new RuntimeException(e);
					
				}
			}
		}
		else
		{
			final Future future = new Future();
//			if(sic.getMethod().getName().indexOf("calcu")!=-1)
//			{
//				System.out.println("cal start: "+sic.getArguments()[0]+" "+ea.getComponentIdentifier());
//				future.addResultListener(new IResultListener()
//				{
//					public void resultAvailable(Object source, Object result)
//					{
//						System.out.println("cal end: "+sic.getArguments()[0]+" "+ea.getComponentIdentifier());
//					}
//					
//					public void exceptionOccurred(Object source, Exception exception)
//					{
//						System.out.println("cal ex: "+sic.getArguments()[0]+" "+ea.getComponentIdentifier());
//					}
//				});
//			}
			
			IFuture resfut = ea.scheduleStep(new InvokeMethodStep(sic, service));
			
			if(scheduleable)
			{
				ret = future;
				resfut.addResultListener(new DelegationResultListener(future));
			}
			else
			{
				System.out.println("Warning, blocking call: "+sic.getMethod());
				ret = resfut.get(new ThreadSuspendable());
//				ret = new Future(null);
			}
		}
		
		sic.setResult(ret);
	}
	
	/**
	 *  Get the standard interceptors for composite service proxies;
	 */
	public static MultiCollection getInterceptors()
	{
		MultiCollection ret = new MultiCollection();
		try
		{
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
	 *  Static method for creating a service proxy.
	 */
	public static IInternalService createServiceProxy(IExternalAccess ea, IComponentAdapter adapter, IInternalService service)
	{
		IServiceIdentifier sid = service.getServiceIdentifier();
		return (IInternalService)Proxy.newProxyInstance(ea.getModel().getClassLoader(), new Class[]{IInternalService.class, sid.getServiceType()}, 
			new BasicServiceInvocationHandler(sid, getInterceptors(), new DecouplingServiceInvocationInterceptor(ea, adapter, service)));
	}
	
	//-------- helper classes --------
	
	/**
	 *  Service invocation step.
	 */
	// Not anonymous class to avoid dependency to XML required for XMLClassname
	public static class InvokeMethodStep implements IComponentStep
	{
		protected ServiceInvocationContext	sic;
		protected Object	service;

		public InvokeMethodStep(ServiceInvocationContext sic, Object service)
		{
			this.sic = sic;
			this.service = service;
		}

		public Object execute(IInternalAccess ia)
		{					
			final Future fut = new Future();
			
			try
			{
				Object res = sic.getMethod().invoke(service, sic.getArguments());
				if(res instanceof IFuture)
				{
					((IFuture)res).addResultListener(new DelegationResultListener(fut));
				}
				else
				{
					// Not correct when not null but some other value.
					fut.setResult(res);
				}
			}
			catch(Exception e)
			{
				fut.setException(e);
			}
			
			return fut;
		}

		public String toString()
		{
			return "invokeMethod("+sic.getMethod()+")";
		}
	}
}
