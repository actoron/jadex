package jadex.extension.rs.invoke;

import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.extension.rs.RSFactory;

/**
 *  Static helper class for rest web service invocation.
 */
public class SRest
{
	/**
	 *  Create a wrapper rest service implementation based on mapping information.
	 *  Components can call this method to create a service implementation that
	 *  uses an external service.
	 *  
	 *  @param agent The internal access of the publishing agent.
	 *  @param type The Jadex interface type of the service to be published.
	 *  @param impl The mapping infomation as annotated interface class.
	 */
	public static Object createServiceImplementation(IInternalAccess agent, Class<?> type, Class<?> impl)
	{
		return ProxyFactory.newProxyInstance(agent.getClassLoader(), new Class[]{type}, 
			RSFactory.getInstance().createRSWrapperInvocationHandler(agent, impl));
	}
}
