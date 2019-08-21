package jadex.micro.testcases.semiautomatic.remoteservice;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.OnInit;

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
	//@AgentCreated
	@OnInit
	public IFuture<Void> agentCreated()
	{
		//agent.getComponentFeature(IProvidedServicesFeature.class)
		agent.getFeature(IProvidedServicesFeature.class).addService("mathservice", IMathService.class, new MathService(agent.getId()), BasicServiceInvocationHandler.PROXYTYPE_DIRECT);
		return IFuture.DONE;
	}
}
