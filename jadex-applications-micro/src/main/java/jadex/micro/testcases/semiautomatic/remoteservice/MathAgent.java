package jadex.micro.testcases.semiautomatic.remoteservice;

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
		addDirectService(new MathService(getServiceProvider()));
		return IFuture.DONE;
	}
}
