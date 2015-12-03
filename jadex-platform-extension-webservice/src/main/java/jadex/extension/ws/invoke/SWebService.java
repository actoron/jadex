package jadex.extension.ws.invoke;

import java.lang.reflect.Proxy;

import jadex.bridge.IInternalAccess;

/**
 * 
 */
public class SWebService
{
	/**
	 *  Create a wrapper service implementation based on the JAXB generated
	 *  Java service class and the service mapping information.
	 */
	public static Object createServiceImplementation(IInternalAccess agent, Class<?> type, WebServiceMappingInfo mapping)
	{
		return Proxy.newProxyInstance(agent.getClassLoader(), new Class[]{type}, 
			new WebServiceWrapperInvocationHandler(agent, mapping));
	}
}
