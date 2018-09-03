package jadex.platform.service.simulation;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;

/**
 *  Agent that counts and stores values in static list.
 */
@Agent
public class CounterAgent
{
	/** The list with counted values. */
	protected static List<String>	LIST	= new ArrayList<>();
	
	/** The initial wait offset. */
	@AgentArgument
	protected long	offset	= 0;
	
	/**
	 *  Count to ten.
	 */
	@AgentBody
	public void count(IInternalAccess agent)
	{
		agent.getFeature(IExecutionFeature.class).waitForDelay(offset).get();
		for(int i=1; i<=10; i++)
		{
			agent.getFeature(IExecutionFeature.class).waitForDelay(1000).get();
			LIST.add(Integer.toString(i));
			System.out.println(agent+" counts "+i);
		}
		agent.killComponent();
	}
}
