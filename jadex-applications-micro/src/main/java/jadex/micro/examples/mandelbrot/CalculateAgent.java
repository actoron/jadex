package jadex.micro.examples.mandelbrot;

import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

/**
 * 
 */
public class CalculateAgent extends MicroAgent
{
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		addService(new CalculateService(getServiceProvider()));
	}
	
	//-------- static methods --------

	/**
	 *  Get the meta information about the agent.
	 */
	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("Agent offering a calculate service.", null, null,
			null, null, null,
			new Class[]{}, new Class[]{ICalculateService.class});
	}
}
