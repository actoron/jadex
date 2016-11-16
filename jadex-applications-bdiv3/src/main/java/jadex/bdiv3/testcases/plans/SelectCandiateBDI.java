package jadex.bdiv3.testcases.plans;

import java.util.List;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalSelectCandidate;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
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
 * 
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class SelectCandiateBDI
{
	/** The bdi agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The test report. */
	protected TestReport tr = new TestReport("#1", "Test if custom plan selection works");
	
	/**
	 *  Some goal.
	 */
	@Goal
	public static class MyGoal
	{
		@GoalSelectCandidate
		protected Object selectCandidate(List<Object> candidates)
		{
			for(Object cand: candidates)
			{
				System.out.println("Candidate: "+cand);
			}
			return candidates.get(0);
		}
	}
	
	/**
	 *  Plan A.
	 */
	@Plan(trigger=@Trigger(goals=MyGoal.class))
	protected void planA(MyGoal goal)
	{
		System.out.println("Plan A");
	}
	
	/**
	 *  Plan B.
	 */
	@Plan(trigger=@Trigger(goals=MyGoal.class))
	protected void planB(MyGoal goal)
	{
		System.out.println("Plan B");
	}
	
	/**
	 *  Plan C.
	 */
	@Plan(trigger=@Trigger(goals=MyGoal.class))
	protected void planC(MyGoal goal)
	{
		System.out.println("Plan B");
	}
	
	/**
	 *  Agent body with behavior code.
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();

		final TestReport tr = new TestReport("#1", "Test if rebuild works with.");
		
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(2000, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(!tr.isFinished())
				{
					tr.setFailed("Goal did not return");
					agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
				}
				
				ret.setResultIfUndone(null);
				return IFuture.DONE;
			}
		});
		
		MyGoal g = new MyGoal();
		agent.getComponentFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(g).get();
		tr.setSucceeded(true);
		agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
		ret.setResultIfUndone(null);
		
		return ret;
	}
}
