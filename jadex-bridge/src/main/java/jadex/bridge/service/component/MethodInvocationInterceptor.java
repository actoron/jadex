package jadex.bridge.service.component;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
class MethodInvocationInterceptor extends AbstractApplicableInterceptor
{
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture execute(ServiceInvocationContext sic)
	{
		try
		{
			Object res = sic.getMethod().invoke(sic.getObject(), sic.getArgumentArray());
			sic.setResult(res);
		}
		catch(Exception e)
		{
			System.out.println("e: "+sic.getMethod()+" "+sic.getObject()+" "+sic.getArgumentArray());
			e.printStackTrace();
			
			if(sic.getMethod().getReturnType().equals(IFuture.class))
			{
				Future fut = new Future();
				fut.setException(e);
				sic.setResult(fut);
			}
			else
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return IFuture.DONE;
	}
}