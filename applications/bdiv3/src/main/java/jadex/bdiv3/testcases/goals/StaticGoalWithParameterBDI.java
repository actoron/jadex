package jadex.bdiv3.testcases.goals;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Agent that tests rebuild in connection with ExcludeMode.WhenTried.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class StaticGoalWithParameterBDI
{
	/** The bdi agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The test report. */
	protected TestReport tr = new TestReport("#1", "Test if static inner goals work");
	
	/**
	 *  A test goal.
	 */
	@Goal(excludemode=ExcludeMode.Never)
	public static class SomeGoal
	{
		@GoalParameter
		protected int cnt;
		
		/**
		 *  Create a new goal.
		 */
		public SomeGoal(int cnt)
		{
			// This assignment is tested because it provokes a change event which cannot access the agent field
			this.cnt = cnt;
		}
		
		/**
		 *  Check the target condition.
		 */
		@GoalTargetCondition
		protected boolean checkTarget()
		{
			return cnt == 2;
		}
		
		/**
		 *  Decrease the cnt.
		 */
		public void decrease()
		{
			System.out.println("cnt was: "+cnt);
			cnt--;
		}
	}
	
	/**
	 *  The plan.
	 */
	@Plan(trigger=@Trigger(goals=SomeGoal.class))
	protected void planA(SomeGoal goal)
	{
		System.out.println("Plan A");
		goal.decrease();
	}
	
	/**
	 *  Agent body with behavior code.
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();

		final TestReport tr = new TestReport("#1", "Test if rebuild works with.");
		
		agent.getFeature(IExecutionFeature.class).waitForDelay(2000, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(!tr.isFinished())
				{
					tr.setFailed("Goal did not return");
					agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
				}
				
				ret.setResultIfUndone(null);
				return IFuture.DONE;
			}
		});
		
		SomeGoal sg = new SomeGoal(3);
		agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(sg).get();
		tr.setSucceeded(true);
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
		ret.setResultIfUndone(null);
		
		return ret;
	}
}
