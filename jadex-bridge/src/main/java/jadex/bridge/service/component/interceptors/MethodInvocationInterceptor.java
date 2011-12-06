package jadex.bridge.service.component.interceptors;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 *  Calls a methods on an object and returns the result.
 */
public class MethodInvocationInterceptor extends AbstractApplicableInterceptor
{
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(ServiceInvocationContext sic)
	{
		try
		{
			boolean	setcaller	= false;
			if(!Proxy.isProxyClass(sic.getObject().getClass()) && IComponentIdentifier.CALLER.get()==null)
				// && sic.getMethod().getName().indexOf("Area")!=-1)
			{
//				if(IComponentIdentifier.LOCAL.get()==null)
//					System.out.println("No caller information: "+sic.getMethod());
				IComponentIdentifier.CALLER.set(IComponentIdentifier.LOCAL.get());
				setcaller	= true;
			}			
			Object res = sic.getMethod().invoke(sic.getObject(), sic.getArgumentArray());
			if(setcaller)
				IComponentIdentifier.CALLER.set(null);
			sic.setResult(res);
		}
		catch(Exception e)
		{
//			System.out.println("e: "+sic.getMethod()+" "+sic.getObject()+" "+sic.getArgumentArray());
//			e.printStackTrace();
			
			if(sic.getMethod().getReturnType().equals(IFuture.class))
			{
				Future fut = new Future();
				Throwable	t	= e instanceof InvocationTargetException
					? ((InvocationTargetException)e).getTargetException() : e;
				fut.setException(t instanceof Exception ? (Exception)t : e);
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