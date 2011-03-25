package jadex.bridge.service.component;

import jadex.commons.future.IFuture;

import java.util.Map;

/**
 *  Abstract interceptor that supports sub interceptors for special cases.
 *  It will perform a lookup for a special case interceptor via its method name.
 */
public abstract class AbstractMultiInterceptor implements IServiceInvocationInterceptor
{
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public final IFuture execute(ServiceInvocationContext sic) 	
	{
		IFuture ret;
		IServiceInvocationInterceptor subic = getInterceptor(sic);
		if(subic!=null)
		{
			ret = subic.execute(sic);
		}
		else
		{
			ret = doExecute(sic);
		}
		return ret;
	}
	
	/**
	 *  Test if the interceptor is applicable.
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(ServiceInvocationContext context)
	{
		return true;
	}
	
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public abstract IFuture doExecute(ServiceInvocationContext sic);
	
	/**
	 *  Get a sub interceptor for special cases.
	 *  @param sic The context.
	 *  @return The interceptor (if any).
	 */
	public IServiceInvocationInterceptor getInterceptor(ServiceInvocationContext sic)
	{
		return null;
	}
}
