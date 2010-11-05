package jadex.micro.examples.compositeservice;

import jadex.micro.MicroAgent;

/**
 * 
 */
public class CalculatorAgent extends MicroAgent
{
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		addService(new AddService(getServiceProvider()));
		addService(new SubService(getServiceProvider()));
	}
}
