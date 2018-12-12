package jadex.bdiv3.testcases.goals;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bdiv3.runtime.IPlan;
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
@Agent(type=BDIAgentFactory.TYPE)
@Results(@Result(name="testresults", clazz=Testcase.class))
public class GoalArrayParameterBDI
{
	/** The bdi agent. */
	@Agent
	protected IInternalAccess agent;
		
	/**
	 * 
	 */
	@Goal(excludemode=ExcludeMode.WhenFailed)
	public class TestGoal
	{
		@GoalParameter
		protected String[] elems = new String[3];
		
		@GoalTargetCondition(parameters="elems")
		public boolean checkTarget()
		{
			return "c".equals(elems[2]);
		}
		
		public void set(int idx, String val)
		{
			elems[idx] = val;
			System.out.println("set: "+idx+" "+val);
		}
	}
	
	@Plan(trigger=@Trigger(goals=TestGoal.class))
	public void inc(TestGoal g, IPlan plan)
	{
		g.set(0, "a");
		g.set(1, "b");
		g.set(2, "c");
//		plan.waitFor(200).get();
		agent.getFeature(IExecutionFeature.class).waitForDelay(200).get();
//		System.out.println("plan end");
	}
	
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();

		final TestReport tr = new TestReport("#1", "Test if a goal condition can be triggered by a goal parameter.");
		
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
		
		agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new TestGoal()).get();
		tr.setSucceeded(true);
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
		ret.setResultIfUndone(null);
		
		return ret;
	}
}

