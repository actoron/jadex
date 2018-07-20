package jadex.bdiv3.testcases.semiautomatic;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.BDIConfiguration;
import jadex.bdiv3.annotation.BDIConfigurations;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.rules.eca.annotations.Event;

/**
 *  Simple agent with inline count goal.
 */
@Agent(type=BDIAgentFactory.TYPE)
@BDIConfigurations(
	@BDIConfiguration(name="first", initialgoals=@NameValue(name="Count2Goal", clazz=GoalBDI.Count2Goal.class)) // todo: allow simple name
)
public class GoalBDI
{
	/**
	 *  Goal with target and drop condition.
	 */
	@Goal(excludemode=ExcludeMode.Never)
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

//		@MRActivated
//		protected boolean activate()
//		{			
//		}
		
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
//		@MRStarted
//		protected void mrStarted()
//		{
//			// default: create apl, select plans, execute
////			APL apl = createAPL();
//		}
//		
//		@MRFinished
//		protected void mrFinished()
//		{
//			// default: -> paused or mrStart
//		}
		
	}
	
	/** The agent. */
	@Agent(type=BDIAgentFactory.TYPE)
	protected IInternalAccess agent;
	
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


