package jadex.bdiv3.testcases.goals;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalParameter;
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
 *  Test if changes of goal multi parameters can be detected in goal conditions.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class GoalPrimitiveArrayParameterBDI
{
	/** The bdi agent. */
	@Agent
	protected IInternalAccess agent;

	@Belief
	protected int[] elems = new int[3];
		
	/**
	 * 
	 */
	@Goal(excludemode=ExcludeMode.WhenFailed)
	public class TestGoal
	{
		@GoalParameter
		protected int[] elems = new int [3];
		
		@GoalCreationCondition(beliefs="elems")
		public TestGoal(int[] elems)
		{
			this.elems = elems;
		}
		
	}
	
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();

		final TestReport tr = new TestReport("#1", "Test if a goal condition can be triggered by injected primitive array goal parameter.");
		
		agent.getFeature(IExecutionFeature.class).waitForDelay(2000, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(!tr.isFinished())
				{
					tr.setFailed("Goal did return");
					agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
				}
				
				ret.setResultIfUndone(null);
				return IFuture.DONE;
			}
		});
		
		tr.setSucceeded(true);
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
		ret.setResultIfUndone(null);
		
		return ret;
	}
}

