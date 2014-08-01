package jadex.micro.testcases.semiautomatic.remoteservice;

import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;

/**
 *  Simple agent that offers the math service.
 */
public class MathAgent extends MicroAgent
{
	/**
	 *  Called once after agent creation.
	 */
	public IFuture	agentCreated()
	{
		addService("mathservice", IMathService.class, new MathService((IServiceProvider)getServiceContainer()), BasicServiceInvocationHandler.PROXYTYPE_DIRECT);
		return IFuture.DONE;
	}
}
