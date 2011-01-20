package jadex.micro.examples.mandelbrot;

import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentManagementService;
import jadex.commons.service.RequiredServiceInfo;
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
		return new MicroAgentMetaInfo("Agent offering a generate service.", null,
			new IArgument[]{new Argument("delay", "Created calculate agents kill themselves when no job arrives in the delay interval.", "Long", new Long(5000))},
			null, null, null,
			new RequiredServiceInfo[]{
				new RequiredServiceInfo("displayservice", IDisplayService.class), 
				new RequiredServiceInfo("calculateservices", ICalculateService.class, true, true, RequiredServiceInfo.SCOPE_GLOBAL),
				new RequiredServiceInfo("cmsservice", IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM),
				new RequiredServiceInfo("generateservice", IGenerateService.class, false, false)
			},
		new Class[]{IGenerateService.class});
	}
}
