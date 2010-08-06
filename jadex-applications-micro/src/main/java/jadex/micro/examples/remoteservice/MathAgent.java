package jadex.micro.examples.remoteservice;

import jadex.micro.MicroAgent;

/**
 *  Simple agent that offers the math service.
 */
public class MathAgent extends MicroAgent
{
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		addService(new MathService(getServiceProvider()));
	}
}
