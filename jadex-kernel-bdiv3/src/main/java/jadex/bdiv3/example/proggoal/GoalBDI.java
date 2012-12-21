package jadex.bdiv3.example.proggoal;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.BDIConfiguration;
import jadex.bdiv3.annotation.BDIConfigurations;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.runtime.APL;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.NameValue;
import jadex.rules.eca.annotations.Event;

/**
 *  Simple agent with inline count goal.
 */
@Agent
@BDIConfigurations(
	@BDIConfiguration(name="first", initialgoals=@NameValue(name="Count2Goal", clazz=GoalBDI.Count2Goal.class)) // todo: allow simple name
)
public class GoalBDI
{
	/**
	 *  Goal with target and drop condition.
	 */
	@Goal(excludemode=MGoal.EXCLUDE_NEVER)
	public class Count2Goal
	{
		/** The target value. */
		protected int target;
		
		/** The drop value. */
		protected int drop;
		
		/**
		 *  Create a new count goal.
		 */
		public Count2Goal()
		{
			this(3, 4);
		}
		
		/**
		 *  Create a new count goal.
		 */
		public Count2Goal(int target, int drop)
		{
			this.target = target;
			this.drop = drop;
		}
		
		/**
		 *  Called whenever the counter belief changes.
		 */
		@GoalTargetCondition
		protected boolean target(@Event("counter") int cnt)
		{
			System.out.println("check target: "+cnt);
			return cnt==target;
		}
		
		/**
		 *  Called whenever the counter belief changes.
		 */
		@GoalDropCondition
		protected boolean drop(@Event("counter") int cnt)
		{
			return cnt==drop;
		}

		
//		@MRCreateAPL
//		protected APL createAPL(APL apl)
//		{
//		}
//		
//		@MRSelectPlan
//		protected RPlan selectPlan(APL apl)
//		{
//		}
//		
//		@MRPlanFinished
//		protected void planFinished(RPlan plan)
//		{
//		}
//		
//		@MRFinished
//		protected void mrFinished()
//		{
//		}
		
	}
	
	/** The agent. */
	@Agent
	protected BDIAgent agent;
	
	/** The counter belief. */
	@Belief
	private int counter;
	
	/**
	 *  Inline plan method that reacts on count goal.
	 */
	@Plan(trigger=@Trigger(goals=Count2Goal.class))
	protected IFuture<Void> inc(Count2Goal goal)
	{
		counter++;
		System.out.println("counter is: "+counter);
		return IFuture.DONE;
	}
}


