package jadex.extension.rs.invoke;

import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.extension.rs.RSFactory;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;

/**
 *  Convenience agent that wraps a normal rest web service as Jadex service.
 *  In this way the web service can be used by active components
 *  in the same way as normal Jadex component services.
 */
@Agent
@ComponentTypes(@ComponentType(name="invocation", filename="jadex/extension/rs/invoke/RestServiceInvocationAgent.class"))
public class RestServiceAgent
{
	//-------- attributes --------
	
	/** The micro agent. */
	@Agent
	protected IInternalAccess agent;
	
	//-------- methods --------
	
	/**
	 *  Create a wrapper service implementation based on mapping information.
	 */
	public Object createServiceImplementation(Class<?> type, Class<?> impl)
	{
		return ProxyFactory.newProxyInstance(agent.getClassLoader(), new Class[]{type}, 
			RSFactory.getInstance().createRSWrapperInvocationHandler(agent, impl));
	}
}
