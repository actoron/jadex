package jadex.platform.service.simulation;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.IFuture;
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
	protected int	offset	= 0;
	
	/** The count increment. */
	@AgentArgument
	protected int	increment	= 3;
	
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
//		System.err.println(agent+" started at "+start);
		
		for(int i=0; i<3; i++)
		{
			// Wait for next time point.
			long	wait	= i==0 ? offset : increment;
//			System.err.println(agent+" wait for "+wait);
			agent.getFeature(IExecutionFeature.class).waitForDelay(wait).get();
			
			// Do/wait some steps to check if clock stays at time point
			for(int step=0; step<3; step++)
			{
				agent.scheduleStep(ia ->
				{
//					long	time	= clock.getTime() - start;
//					System.err.println(agent+" step at "+time);
					return IFuture.DONE;
				}).get();
			}
			
			long	time	= clock.getTime() - start;
			LIST.add(Long.toString(time));
//			System.err.println(agent+" counts at "+time);
			
			// Do/wait some steps to check if clock stays at time point
			for(int step=0; step<3; step++)
			{
				agent.scheduleStep(ia ->
				{
//					long	time1	= clock.getTime() - start;
//					System.err.println(agent+" step at "+time1);
					return IFuture.DONE;
				}).get();
			}
		}
		agent.killComponent();
	}
}
