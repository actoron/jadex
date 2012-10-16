package jadex.bdiv3.example.counter;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

@Agent
public class CountBDI
{
	@Agent
	protected BDIAgent agent;
	
	@Belief
	private int counter;
	
	@AgentBody
	public void body()
	{
		agent.adoptGoal(new CountGoal(10));
		System.out.println("body end: "+getClass().getName());
	}
	
	@Plan(trigger=@Trigger(goals=CountGoal.class))
	protected IFuture<Void> inc(CountGoal goal)
	{
		counter++;
		System.out.println("counter is: "+counter);
		return IFuture.DONE;
	}
}
