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
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.rules.eca.annotations.Event;

/**
 *  Simple agent with inline count goal.
 */
@Agent(type=BDIAgentFactory.TYPE)
@BDIConfigurations(
	@BDIConfiguration(name="first", initialgoals=@NameValue(name="Count2Goal", clazz=Count2BDI.Count2Goal.class)) // todo: allow simple name
)
public class Count2BDI
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The counter belief. */
	@Belief
	private int counter;
	
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
			System.out.println("check target: "+cnt+" "+target);
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
	}
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		IFuture<Count2Goal> fut = agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new Count2Goal(5, 10));
		fut.addResultListener(new IResultListener<Count2Goal>()
		{
			public void resultAvailable(Count2Goal goal)
			{
				System.out.println("My goal succeeded: "+goal);
			}
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("My goal failed: "+exception);
			}
		});
		
		System.out.println("body end: "+getClass().getName());
	}
	
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


