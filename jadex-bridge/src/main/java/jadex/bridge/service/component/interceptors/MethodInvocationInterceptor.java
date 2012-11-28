package jadex.bridge.service.component.interceptors;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.BasicServiceContainer;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 *  Calls a methods on an object and returns the result.
 */
public class MethodInvocationInterceptor extends AbstractApplicableInterceptor
{
	//-------- attributes --------
	
	/** The realtime timeout flag. */
	protected boolean	realtime;
	
	//-------- constructors --------
	
	/**
	 *  Create a method invocation interceptor.
	 */
	public MethodInvocationInterceptor()
	{
		// Todo real time!?
		this.realtime	= false;
	}
	
	//-------- methods --------
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(ServiceInvocationContext sic)
	{
		try
		{
			boolean	setcaller	= false;
			if(!Proxy.isProxyClass(sic.getObject().getClass()) && ServiceCall.getCurrentInvocation()==null)
				// && sic.getMethod().getName().indexOf("Area")!=-1)
			{
				long to = BasicServiceContainer.getMethodTimeout(
					sic.getObject().getClass().getInterfaces(), sic.getMethod(), sic.isRemoteCall());
				Map<String, Object> props = new HashMap<String, Object>();
				props.put(ServiceCall.TIMEOUT, new Long(to));
				props.put(ServiceCall.REALTIME, realtime? Boolean.TRUE: Boolean.FALSE);
				CallStack.push(IComponentIdentifier.LOCAL.get(), props);
//				CallStack.push(IComponentIdentifier.LOCAL.get(), to, realtime);
				setcaller	= true;
			}			
			Object res = sic.getMethod().invoke(sic.getObject(), sic.getArgumentArray());
			if(setcaller)
			{
				CallStack.pop();
			}
			sic.setResult(res);
		}
		catch(Exception e)
		{
//			System.out.println("e: "+sic.getMethod()+" "+sic.getObject()+" "+sic.getArgumentArray());
//			e.printStackTrace();
			
			if(sic.getMethod().getReturnType().equals(IFuture.class))
			{
				Future<?> fut = new Future();
				Throwable	t	= e instanceof InvocationTargetException
					? ((InvocationTargetException)e).getTargetException() : e;
				fut.setException(t instanceof Exception ? (Exception)t : new RuntimeException(t));
				sic.setResult(fut);
			}
			else
			{
//				e.printStackTrace();
				Throwable	t	= e instanceof InvocationTargetException
					? ((InvocationTargetException)e).getTargetException() : e;
				throw t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
//				{
//					public void printStackTrace()
//					{
//						Thread.dumpStack();
//						super.printStackTrace();
//					}
//				};
			}
		}
		
		return IFuture.DONE;
	}
}