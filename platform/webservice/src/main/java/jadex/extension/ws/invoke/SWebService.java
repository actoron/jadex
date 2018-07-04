package jadex.extension.ws.invoke;

import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;

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
		return ProxyFactory.newProxyInstance(agent.getClassLoader(), new Class[]{type}, 
			new WebServiceWrapperInvocationHandler(agent, mapping));
	}
}
