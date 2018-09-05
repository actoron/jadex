package jadex.platform.service.simulation;


import jadex.bridge.service.types.simulation.ISimulationService;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Autostart;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;

/**
 *  Agent that provides the simulation service.
 */
@Agent(autostart=@Autostart(Boolean3.TRUE))
@Arguments(@Argument(name="bisimulation", clazz=boolean.class))
@ProvidedServices(@ProvidedService(type=ISimulationService.class, scope=RequiredService.SCOPE_PLATFORM, implementation=@Implementation(expression="SimulationAgent.create($args.bisimulation)")))
//@Properties(value=@NameValue(name="system", value="true"))
public class SimulationAgent
{
	/** Global simulation service instance for bisimulation, if any. */
	protected static volatile ISimulationService	bisimservice;
	
	/**
	 *  Create the simulation service or check for bisimulation.
	 */
	public static ISimulationService	create(Object bisimulation)
	{
		if(Boolean.TRUE.equals(bisimulation))
		{
			synchronized(SimulationAgent.class)
			{
				if(bisimservice!=null)
				{
					// Only the first platform has the simulation service.
					// todo: what if first platform gets shut down first???
					return null;
				}
				else
				{
					bisimservice	= new SimulationService();
					return bisimservice;
				}
			}
		}
		else
		{
			return new SimulationService();
		}
	}
}
