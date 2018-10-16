package jadex.platform.service.simulation;


import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.service.types.simulation.ISimulationService;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;

/**
 *  Agent that provides the simulation service.
 */
@Agent(autostart=Boolean3.TRUE)
@Arguments(@Argument(name="bisimulation", clazz=boolean.class))
@ProvidedServices(@ProvidedService(type=ISimulationService.class, scope=RequiredService.SCOPE_PLATFORM, implementation=@Implementation(expression="SimulationAgent.create($args.bisimulation)")))
//@Properties(value=@NameValue(name="system", value="true"))
public class SimulationAgent
{
	/** Global simulation service instance for bisimulation, if any. */
	protected static volatile ISimulationService	bisimservice;
	protected static volatile boolean	started;
	
	/**
	 *  Create the simulation service or check for bisimulation.
	 */
	public static ISimulationService	create(Object bisimulation)
	{
		if(Boolean.TRUE.equals(bisimulation))
		{
			boolean	create	= false;
			synchronized(SimulationAgent.class)
			{
				if(!started)
				{
					// On first start -> create extra platform for running the sim service singleton.
					started	= true;
					create	= true;
				}
			}
			if(create)
				Starter.createPlatform(STest.getDefaultTestConfig(), new String[]{"-bisimulation", "true"}).get();
					
			synchronized(SimulationAgent.class)
			{
				if(bisimservice!=null)
				{
					// Only the first platform has the simulation service.
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
