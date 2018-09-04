package jadex.platform.service.clock;


import jadex.bridge.service.types.clock.IClockService;
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
 *  Agent that provides the clock service.
 */
@Agent(autostart=@Autostart(value=Boolean3.TRUE, predecessors="jadex.platform.service.monitoring.MonitoringAgent"))
@Arguments({
	@Argument(name="simulation", clazz=boolean.class, defaultvalue="false"),
	@Argument(name="bisimulation", clazz=boolean.class, defaultvalue="false")})
@ProvidedServices(@ProvidedService(type=IClockService.class, scope=RequiredService.SCOPE_PLATFORM, implementation=@Implementation(
	expression="$args.simulation==null || !$args.simulation.booleanValue()? new jadex.platform.service.clock.ClockService(new jadex.platform.service.clock.ClockCreationInfo(jadex.bridge.service.types.clock.IClock.TYPE_SYSTEM, \"system_clock\", System.currentTimeMillis(), 100), $component, $args.simulation): new jadex.platform.service.clock.ClockService(new jadex.platform.service.clock.ClockCreationInfo(jadex.bridge.service.types.clock.IClock.TYPE_EVENT_DRIVEN, \"simulation_clock\", System.currentTimeMillis(), 100), $component, $args.simulation)", proxytype=Implementation.PROXYTYPE_RAW)))
//@Properties(value=@NameValue(name="system", value="true"))
public class  ClockAgent
{
	// does not work
//	public final static String CLASSNAME = MonitoringAgent.class.getName();
	
//	@AgentCreated
//	public void started()
//	{
//		System.out.println("start");
//	}
//	
//	@AgentKilled
//	public void killed(Exception e)
//	{
//		e.printStackTrace();
//	}
}

