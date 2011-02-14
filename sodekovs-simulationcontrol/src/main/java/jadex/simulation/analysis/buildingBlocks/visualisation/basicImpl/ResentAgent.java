package jadex.simulation.analysis.buildingBlocks.visualisation.basicImpl;

import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.simulation.analysis.buildingBlocks.visualisation.IPresentResultService;

@Description("This agent is a minimal result presenter.")
@ProvidedServices({
	@ProvidedService(type=IPresentResultService.class, expression="new BasicPresentResultService($generalComp)")}
)
public class ResentAgent extends MicroAgent
{
	
}
