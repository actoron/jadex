package jadex.bridge;

/**
 *  Service invocation interceptor interface.
 */
public interface IServiceInvocationInterceptor
{
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public void execute(ServiceInvocationContext context); 
}
