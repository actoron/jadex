package jadex.micro.testcases.semiautomatic.remoteservice;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;

/**
 *  Simple agent that offers the math service.
 */
@Agent
public class MathAgent
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Called once after agent creation.
	 */
	@AgentCreated
	public IFuture<Void> agentCreated()
	{
		//agent.getComponentFeature(IProvidedServicesFeature.class)
		agent.getFeature(IProvidedServicesFeature.class).addService("mathservice", IMathService.class, new MathService(agent.getIdentifier()), BasicServiceInvocationHandler.PROXYTYPE_DIRECT);
		return IFuture.DONE;
	}
}
