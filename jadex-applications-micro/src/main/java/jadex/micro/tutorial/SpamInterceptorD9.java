package jadex.micro.tutorial;

import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Simple interceptor that refuses messages of spammers.
 */
public class SpamInterceptorD9 implements IServiceInvocationInterceptor
{
	/**
	 *  Test if the interceptor is applicable.
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(ServiceInvocationContext context)
	{
		try
		{
			return context.getMethod().getName().equals("message");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture execute(ServiceInvocationContext context)
	{
		if(((String)context.getArgumentArray()[0]).startsWith("Spammer"))
		{
			return new Future((new RuntimeException("No spammers allowed.")));
		}
		else
		{
			return context.invoke();
		}
	}
}
