package jadex.platform.service.simulation;


import jadex.bridge.service.types.simulation.ISimulationService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that provides the simulation service.
 */
@Agent
@ProvidedServices(@ProvidedService(type=ISimulationService.class, scope=Binding.SCOPE_PLATFORM, implementation=@Implementation(SimulationService.class)))
//@Properties(value=@NameValue(name="system", value="true"))
public class SimulationAgent
{
}
