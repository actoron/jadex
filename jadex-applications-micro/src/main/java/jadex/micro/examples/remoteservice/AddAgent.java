package jadex.micro.examples.remoteservice;

import jadex.micro.MicroAgent;

/**
 *  Simple agent that offers the add service.
 */
public class AddAgent extends MicroAgent
{
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		addService(IAddService.class, new AddService(getServiceProvider(), "addservice"));
		startServiceProvider();
	}
}
