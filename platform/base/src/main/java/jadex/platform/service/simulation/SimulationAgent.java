package jadex.platform.service.simulation;


import jadex.bridge.IInternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.types.simulation.ISimulationService;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.platform.PlatformAgent;

/**
 *  Agent that provides the simulation service.
 */
@Agent(autostart=Boolean3.TRUE)
@Arguments({
	@Argument(name="simfactory", clazz=Object.class)
})
@ProvidedServices(@ProvidedService(type=ISimulationService.class, scope=ServiceScope.PLATFORM,
	implementation=@Implementation(expression="SimulationAgent.create($component)")))
//@Properties(value=@NameValue(name="system", value="true"))
public class SimulationAgent
{
	/**
	 *  Create the simulation service.
	 */
	public static ISimulationService	create(IInternalAccess component)
	{
		return PlatformAgent.createMaybeSharedServiceImpl("sim", component, () ->
		{
			SimulationService	simserv	= new SimulationService();
			simserv.access	= component;	// Hack!!! injection not performed for shared/wrapped service instance so do it manually
			return simserv;
		});
	}
}
