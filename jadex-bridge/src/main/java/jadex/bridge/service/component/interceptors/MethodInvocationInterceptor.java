package jadex.bridge.service.component.interceptors;

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
	//-------- methods --------
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(ServiceInvocationContext sic)
	{
//		if(sic.getMethod().getName().equals("ma1"))
//			System.out.println("ggghhh");
		
		try
		{
			// Must set nextinvoc and service call as it is not unknown if
			// a) the method is directly the business logic or
			// b) the method jumps from required to provided interceptor chain
				
//			if(sic.getMethod().getName().indexOf("getChildren")!=-1)
//				System.out.println("ggggg");
			
			// Problem that the object could be an rmi proxy itself that delegates the call
			// Is this case the switch (current becomes next) must not occur
			
			// Hack, user may want to use proxy as well :-(
			// todo:
			boolean switchcall = !Proxy.isProxyClass(sic.getObject().getClass());
			 
			// is not sufficient as could also be basicinvocationhandler of provided proxy
//			if(Proxy.isProxyClass(sic.getObject().getClass()))
//			{
//				Object handler = Proxy.getInvocationHandler(sic.getObject());
//				if(handler.getClass().getName().indexOf("RemoteMethodInvocationHandler")!=-1) // HACK!!!
//				{
//					switchcall = false;
//				}
//			}
			
			if(switchcall)
			{
//				if(sic.getMethod().getName().indexOf("test")!=-1)
//					System.out.println("setting to a: "+sic.getServiceCall());
				CallAccess.setServiceCall(sic.getServiceCall()); // next becomes current
				CallAccess.resetNextInvocation();
			}
			else
			{
//				if(sic.getMethod().getName().indexOf("test")!=-1)
//					System.out.println("setting to b: "+sic.getLastServiceCall());
				CallAccess.setServiceCall(sic.getLastServiceCall());
				CallAccess.setNextInvocation(sic.getServiceCall());
			}
			
			Object res = sic.getMethod().invoke(sic.getObject(), sic.getArgumentArray());

//			if(sic.getMethod().getName().indexOf("test")!=-1)
//				System.out.println("setting to c: "+sic.getLastServiceCall());
			CallAccess.setServiceCall(sic.getLastServiceCall()); 
			CallAccess.resetNextInvocation();
			
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