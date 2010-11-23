package jadex.micro.examples.mandelbrot;

import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

/**
 *  Agent offering a display service.
 */
public class DisplayAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The gui . */
	protected DisplayPanel	panel;
	
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
//		addService(new DisplayService(getServiceProvider()));
	}
	
	//-------- static methods --------

	/**
	 *  Get the meta information about the agent.
	 */
	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("Agent offering a display service.", null, null,
			null, null, null,
			new Class[]{}, new Class[]{IDisplayService.class});
	}
}
