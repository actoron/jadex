package jadex.bdiv3.example.counter;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.model.MGoal;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.rules.eca.annotations.Event;

/**
 *  Does not work as javassist does not support inner classes :-(
 */
@Agent
public class Count2BDI
{
	@Goal(excludemode=MGoal.EXCLUDE_NEVER)
	public class Count2Goal
	{
		protected int target;
		
		/**
		 *  Create a new count goal.
		 */
		public Count2Goal(int target)
		{
			this.target = target;
		}
		
		@GoalTargetCondition
		protected boolean target(@Event("counter") int cnt)
		{
			return cnt==target;
		}
	}
	
	@Agent
	protected BDIAgent agent;
	
	@Belief
	private int counter;
	
	@AgentBody
	public void body()
	{
		agent.dispatchGoalAndWait(new Count2Goal(10));
		System.out.println("body end: "+getClass().getName());
	}
	
	@Plan(trigger=@Trigger(goals=Count2Goal.class))
	protected IFuture<Void> inc(CountGoal goal)
	{
		counter++;
		System.out.println("counter is: "+counter);
		return IFuture.DONE;
	}
}
