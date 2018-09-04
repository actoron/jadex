package jadex.platform.service.simulation;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentServiceSearch;
import jadex.micro.annotation.RequiredService;

/**
 *  Agent that counts and stores values in static list.
 */
@Agent
public class CounterAgent
{
	/** The list with counted values. */
	protected static List<String>	LIST	= new ArrayList<>();
	
	/** The count offset. */
	@AgentArgument
	protected int	offset	= 1;
	
	/** The clock service. */
	@AgentServiceSearch(requiredservice=@RequiredService(name="clock", type=IClockService.class))
	protected IClockService	clock;
	
	/**
	 *  Count to ten.
	 */
	@AgentBody
	public void count(IInternalAccess agent)
	{
		long	start	= clock.getTime();
		
		for(int i=offset; i<=10; i+=offset)
		{
			agent.getFeature(IExecutionFeature.class).waitForDelay(offset).get();
			long	time	= clock.getTime() - start;
			LIST.add(Long.toString(time));
			System.out.println(agent+" counts at "+time);
		}
		agent.killComponent();
	}
}
