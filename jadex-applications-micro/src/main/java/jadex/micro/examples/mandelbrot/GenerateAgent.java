package jadex.micro.examples.mandelbrot;

import jadex.bridge.IComponentManagementService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.micro.MicroAgent;
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
public class GenerateAgent extends MicroAgent
{
}
