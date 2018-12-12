package jadex.bdiv3.testcases.goals;

import java.util.ArrayList;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.impl.PlanFailureException;
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
@Agent(type=BDIAgentFactory.TYPE)
@Results(@Result(name="testresults", clazz=Testcase.class))
public class GoalRebuildBDI
{
	/** The bdi agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The test report. */
	protected TestReport tr = new TestReport("#1", "Test if rebuild works with exclude mode that excludes");
	
	/**
	 *  A test goal.
	 */
	@Goal(rebuild=true)
	public class SomeGoal
	{
		public List<String> plans = new ArrayList<String>();
	}
	
	@Plan(trigger=@Trigger(goals=SomeGoal.class))
	protected void planA(SomeGoal goal)
	{
		System.out.println("Plan A");
		goal.plans.add("A");
		throw new PlanFailureException();
	}
	
	@Plan(trigger=@Trigger(goals=SomeGoal.class))
	protected void planB(SomeGoal goal)
	{
		System.out.println("Plan B");
		goal.plans.add("B");
		throw new PlanFailureException();
	}
	
	@Plan(trigger=@Trigger(goals=SomeGoal.class))
	protected void planC(SomeGoal goal)
	{
		System.out.println("Plan C");
		goal.plans.add("C");
	}
	
	@Plan(trigger=@Trigger(goals=SomeGoal.class))
	protected void planD(SomeGoal goal)
	{
		System.out.println("Plan D");
		goal.plans.add("D");
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
					tr.setFailed("Goal did return");
					agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
				}
				
				ret.setResultIfUndone(null);
				return IFuture.DONE;
			}
		});
		
		SomeGoal sg = new SomeGoal();
		agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(sg).get();
		if(sg.plans.size()==3 && sg.plans.get(0).equals("A") && sg.plans.get(1).equals("B") && sg.plans.get(2).equals("C"))
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setFailed("Wrong plans were executed: "+sg.plans);
		}
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
		ret.setResultIfUndone(null);
		
		return ret;
	}
}
