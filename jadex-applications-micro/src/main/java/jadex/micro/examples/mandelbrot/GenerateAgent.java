package jadex.micro.examples.mandelbrot;

import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

/**
 *  Agent that can process generate requests.
 */
public class GenerateAgent extends MicroAgent
{
	
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		addService(new GenerateService(this, (GeneratePanel)GeneratePanel.createGui(this.getExternalAccess())[1]));		
	}
	
	//-------- static methods --------

	/**
	 *  Get the meta information about the agent.
	 */
	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("Agent offering a generate service.", null, null,
			null, null, null,
			null, new Class[]{IGenerateService.class});
	}
}
