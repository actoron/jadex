package jadex.bridge.service.component;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 *  Invocation interceptor for executing a call on 
 *  the underlying component thread. 
 */
public class DecouplingServiceInvocationInterceptor extends AbstractMultiInterceptor
{
	//-------- constants --------
	
	/** The static map of subinterceptors (method -> interceptor). */
	protected static Map SUBINTERCEPTORS = getInterceptors();
	
	//-------- attributes --------
	
	/** The external access. */
	protected IExternalAccess ea;	
		
	/** The component adapter. */
	protected IComponentAdapter adapter;
	
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public DecouplingServiceInvocationInterceptor(IExternalAccess ea, IComponentAdapter adapter)
	{
		this.ea = ea;
		this.adapter = adapter;
	}
	
	//-------- methods --------
	
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public IFuture doExecute(ServiceInvocationContext sic)
	{
		Future ret = new Future();
		
		boolean scheduleable = sic.getMethod().getReturnType().equals(IFuture.class) 
			|| sic.getMethod().getReturnType().equals(void.class);
		
		if(!adapter.isExternalThread() || !scheduleable)
		{
//			if(sic.getMethod().getName().equals("add"))
//				System.out.println("direct: "+Thread.currentThread());
			sic.invoke().addResultListener(new DelegationResultListener(ret));
		}
		else
		{
//			if(sic.getMethod().getName().equals("add"))
//				System.out.println("decouple: "+Thread.currentThread());
			ea.scheduleStep(new InvokeMethodStep(sic)).addResultListener(new DelegationResultListener(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Get a sub interceptor for special cases.
	 *  @param sic The context.
	 *  @return The interceptor (if any).
	 */
	public IServiceInvocationInterceptor getInterceptor(ServiceInvocationContext sic)
	{
		return (IServiceInvocationInterceptor)SUBINTERCEPTORS.get(sic.getMethod());
	}
	
	/**
	 *  Get the sub interceptors for special cases.
	 */
	public static Map getInterceptors()
	{
		Map ret = new HashMap();
		try
		{
			ret.put(Object.class.getMethod("toString", new Class[0]), new AbstractApplicableInterceptor()
			{
				public IFuture execute(ServiceInvocationContext context)
				{
					Object proxy = context.getProxy();
					InvocationHandler handler = (InvocationHandler)Proxy.getInvocationHandler(proxy);
					context.setResult(handler.toString());
					return IFuture.DONE;
				}
			});
			ret.put(Object.class.getMethod("equals", new Class[]{Object.class}), new AbstractApplicableInterceptor()
			{
				public IFuture execute(ServiceInvocationContext context)
				{
					Object proxy = context.getProxy();
					InvocationHandler handler = (InvocationHandler)Proxy.getInvocationHandler(proxy);
					Object[] args = (Object[])context.getArguments().toArray();
					context.setResult(new Boolean(args[0]!=null && Proxy.isProxyClass(args[0].getClass())
						&& handler.equals(Proxy.getInvocationHandler(args[0]))));
					return IFuture.DONE;
				}
			});
			ret.put(Object.class.getMethod("hashCode", new Class[0]), new AbstractApplicableInterceptor()
			{
				public IFuture execute(ServiceInvocationContext context)
				{
					Object proxy = context.getProxy();
					InvocationHandler handler = Proxy.getInvocationHandler(proxy);
					context.setResult(new Integer(handler.hashCode()));
					return IFuture.DONE;
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
	
	//-------- helper classes --------
	
	/**
	 *  Service invocation step.
	 */
	// Not anonymous class to avoid dependency to XML required for XMLClassname
	public static class InvokeMethodStep implements IComponentStep
	{
		protected ServiceInvocationContext sic;

		public InvokeMethodStep(ServiceInvocationContext sic)
		{
			this.sic = sic;
		}

		public Object execute(IInternalAccess ia)
		{					
			final Future fut = new Future();
			
			try
			{
//				sic.setObject(service);
				sic.invoke().addResultListener(new DelegationResultListener(fut));
			}
			catch(Exception e)
			{
				e.printStackTrace();
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
