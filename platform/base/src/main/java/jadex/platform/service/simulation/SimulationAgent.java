package jadex.platform.service.simulation;


import jadex.bridge.service.types.simulation.ISimulationService;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Autostart;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;

/**
 *  Agent that provides the simulation service.
 */
@Agent(autostart=@Autostart(Boolean3.TRUE))
@ProvidedServices(@ProvidedService(type=ISimulationService.class, scope=RequiredService.SCOPE_PLATFORM, implementation=@Implementation(SimulationService.class)))
//@Properties(value=@NameValue(name="system", value="true"))
public class SimulationAgent
{
}
