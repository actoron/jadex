package jadex.micro.testcases.semiautomatic.compositeservice;

import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 * 
 */
@Description("This agent is a minimal calculator.")
@ProvidedServices({
	@ProvidedService(type=IAddService.class, expression="new AddService($component)"),
	@ProvidedService(type=ISubService.class, expression="new SubService($component)")}
)
public class CalculatorAgent extends MicroAgent
{
//	/**
//	 *  Called once after agent creation.
//	 */
//	public void agentCreated()
//	{
//		addDirectService(new AddService(this));
//		addDirectService(new SubService(this));
//	}
	
	//-------- static methods --------

//	/**
//	 *  Get the meta information about the agent.
//	 */
//	public static MicroAgentMetaInfo getMetaInfo()
//	{
//		return new MicroAgentMetaInfo("This agent is a minimal calculator.", null, null,
//			null, null, SUtil.createHashMap(new String[]{"componentviewer.viewerclass"}, new Object[]{"jadex.micro.examples.helpline.HelplineViewerPanel"}),
//			null, new ProvidedServiceInfo[]{new ProvidedServiceInfo(IAddService.class), new ProvidedServiceInfo(ISubService.class)});
//	}
}
