package jadex.bridge.service.component.interceptors;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.SUtil;
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
			if(!Proxy.isProxyClass(sic.getObject().getClass())
				&& !SUtil.equals(IComponentIdentifier.CALLER.get(), IComponentIdentifier.LOCAL.get()))
				// && sic.getMethod().getName().indexOf("Area")!=-1)
			{
				if(IComponentIdentifier.CALLER.get()==null)
					System.out.println(IComponentIdentifier.CALLER.get()+" invoking "+sic.getMethod().getName()+" on "+IComponentIdentifier.LOCAL.get());
			}			
			Object res = sic.getMethod().invoke(sic.getObject(), sic.getArgumentArray());
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