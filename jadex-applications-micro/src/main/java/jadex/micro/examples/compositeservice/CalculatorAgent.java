package jadex.micro.examples.compositeservice;

import jadex.commons.SUtil;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

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
		addDirectService(new AddService(getServiceProvider()));
		addDirectService(new SubService(getServiceProvider()));
	}
	
	//-------- static methods --------

	/**
	 *  Get the meta information about the agent.
	 */
	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent is a minimal calculator.", null, null,
			null, null, SUtil.createHashMap(new String[]{"componentviewer.viewerclass"}, new Object[]{"jadex.micro.examples.helpline.HelplineViewerPanel"}),
			null, new Class[]{IAddService.class, ISubService.class});
	}
}
