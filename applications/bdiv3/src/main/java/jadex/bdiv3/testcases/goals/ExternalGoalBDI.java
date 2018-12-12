package jadex.bdiv3.testcases.goals;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Goals;
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
 * 
 */
@Agent(type=BDIAgentFactory.TYPE)
@Results(@Result(name="testresults", clazz=Testcase.class))
@Goals(@Goal(clazz=ExternalGoal.class, excludemode=ExcludeMode.Never))
public class ExternalGoalBDI
{
	/** The bdi agent. */
	@Agent(type=BDIAgentFactory.TYPE)
	protected IInternalAccess agent;
	
	/** The test report. */
	protected TestReport tr = new TestReport("#1", "Test if static inner goals work");
	
	/**
	 *  The plan.
	 */
	@Plan(trigger=@Trigger(goals=ExternalGoal.class))
	protected void planA(ExternalGoal goal)
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
		
		ExternalGoal eg = new ExternalGoal(3);
		agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(eg).get();
		tr.setSucceeded(true);
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
		ret.setResultIfUndone(null);
		
		return ret;
	}
}
