package jadex.simulation.analysis.buildingBlocks.visualisation.basicImpl;

import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.simulation.analysis.buildingBlocks.visualisation.IPresentResultService;

@Description("This agent is a minimal result presenter.")
@ProvidedServices({
	@ProvidedService(type=IPresentResultService.class, implementation=@Implementation(BasicPresentResultService.class))
	})
public class PresentAgent extends MicroAgent
{
	
}
