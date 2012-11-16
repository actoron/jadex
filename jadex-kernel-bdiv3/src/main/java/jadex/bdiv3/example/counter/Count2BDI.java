package jadex.bdiv3.example.counter;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.model.MGoal;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
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
		protected int drop;
		
		/**
		 *  Create a new count goal.
		 */
		public Count2Goal(int target, int drop)
		{
			this.target = target;
			this.drop = drop;
		}
		
		@GoalTargetCondition
		protected boolean target(@Event("counter") int cnt)
		{
			System.out.println("check target: "+cnt);
			return cnt==target;
		}
		
		@GoalDropCondition
		protected boolean drop(@Event("counter") int cnt)
		{
			return cnt==drop;
		}
	}
	
	@Agent
	protected BDIAgent agent;
	
	@Belief
	private int counter;
	
	@AgentBody
	public void body()
	{
		agent.dispatchGoalAndWait(new CountGoal(10, 5))
			.addResultListener(new IResultListener<CountGoal>()
		{
			public void resultAvailable(CountGoal goal)
			{
				System.out.println("My goal succeeded: "+goal);
			}
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("My goal failed: "+exception);
			}
		});
		
//		agent.dispatchGoalAndWait(new CountGoal(5, 10))
//			.addResultListener(new DefaultResultListener<CountGoal>()
//		{
//			public void resultAvailable(CountGoal goal)
//			{
//				System.out.println("My goal succeeded: "+goal);
//			}
//		});
		
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


