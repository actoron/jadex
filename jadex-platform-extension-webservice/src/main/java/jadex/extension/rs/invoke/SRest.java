package jadex.extension.rs.invoke;

import jadex.bridge.IInternalAccess;

import java.lang.reflect.Proxy;

/**
 * 
 */
public class SRest
{
	/**
	 *  Create a wrapper service implementation based on mapping information.
	 */
	public static Object createServiceImplementation(IInternalAccess agent, Class<?> type, Class<?> impl)
	{
		return Proxy.newProxyInstance(agent.getClassLoader(), new Class[]{type}, 
			new RestServiceWrapperInvocationHandler(agent, impl));
	}
}
