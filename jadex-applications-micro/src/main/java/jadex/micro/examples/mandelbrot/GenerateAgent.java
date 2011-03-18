package jadex.micro.examples.mandelbrot;

import jadex.bridge.IComponentManagementService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Agent that can process generate requests.
 */
@Description("Agent offering a generate service.")
@ProvidedServices(@ProvidedService(type=IGenerateService.class, expression="new GenerateService($component, (GeneratePanel)GeneratePanel.createGui($component.getExternalAccess())[1])"))
@RequiredServices({
	@RequiredService(name="displayservice", type=IDisplayService.class),
	@RequiredService(name="calculateservices", type=ICalculateService.class, multiple=true, scope=RequiredServiceInfo.SCOPE_GLOBAL),
	@RequiredService(name="cmsservice", type=IComponentManagementService.class, scope=RequiredServiceInfo.SCOPE_PLATFORM),
	@RequiredService(name="generateservice", type=IGenerateService.class)
})
@Arguments(@Argument(name="delay", description="Agent kills itself when no job arrives in the delay interval.", typename="Long", defaultvalue="new Long(5000)"))
public class GenerateAgent extends MicroAgent
{
//	/**
//	 *  Called once after agent creation.
//	 */
//	public void agentCreated()
//	{
//		addService(new GenerateService(this, (GeneratePanel)GeneratePanel.createGui(this.getExternalAccess())[1]));		
//	}
//	
//	//-------- static methods --------
//
//	/**
//	 *  Get the meta information about the agent.
//	 */
//	public static MicroAgentMetaInfo getMetaInfo()
//	{
//		return new MicroAgentMetaInfo("Agent offering a generate service.", null,
//			new IArgument[]{new Argument("delay", "Created calculate agents kill themselves when no job arrives in the delay interval.", "Long", new Long(5000))},
//			null, null, null,
//			new RequiredServiceInfo[]{
//				new RequiredServiceInfo("displayservice", IDisplayService.class), 
//				new RequiredServiceInfo("calculateservices", ICalculateService.class, true, true, RequiredServiceInfo.SCOPE_GLOBAL),
//				new RequiredServiceInfo("cmsservice", IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM),
//				new RequiredServiceInfo("generateservice", IGenerateService.class, false, false)
//			},
//		new ProvidedServiceInfo[]{new ProvidedServiceInfo(IGenerateService.class)});
//	}
}
