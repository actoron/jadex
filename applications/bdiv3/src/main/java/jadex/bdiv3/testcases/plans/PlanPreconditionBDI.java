package jadex.bdiv3.testcases.plans;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

@Agent(type=BDIAgentFactory.TYPE)
@Results(@Result(name="testresults", clazz=Testcase.class))
public class PlanPreconditionBDI
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;

	protected String res = "";
	
	@Goal
	protected class SomeGoal
	{
	}
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		TestReport tr = new TestReport("#1", "Test if plan precondition works.");
		agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new SomeGoal()).get();
		if("ABD".equals(res))
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setFailed("Wrong plans executed: "+res);
		}
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
		agent.killComponent();
	}
	
	@Plan(trigger=@Trigger(goals=SomeGoal.class))
	protected class PlanA
	{
		@PlanPrecondition
		protected boolean precondition()
		{
			return true;
		}
		
		@PlanBody
		protected IFuture<Void> body()
		{
			System.out.println("Plan A");
			res += "A";
			return new Future<Void>(new PlanFailureException());
//			return IFuture.DONE;
		}
	}
	
	@Plan(trigger=@Trigger(goals=SomeGoal.class))
	protected IFuture<Void> planB()
	{
		System.out.println("Plan B");
		res += "B";
		return new Future<Void>(new PlanFailureException());
//			return IFuture.DONE;
	}
	
	@Plan(trigger=@Trigger(goals=SomeGoal.class))
	protected class PlanC
	{
		@PlanPrecondition
		protected IFuture<Boolean> precondition()
		{
			return new Future<Boolean>(Boolean.FALSE);
		}
		
		@PlanBody
		protected IFuture<Void> body()
		{
			System.out.println("Plan C");
			res += "C";
			return IFuture.DONE;
		}
	}
	
	@Plan(trigger=@Trigger(goals=SomeGoal.class))
	protected class PlanD
	{
		@PlanPrecondition
		protected IFuture<Boolean> precondition()
		{
			return new Future<Boolean>(Boolean.TRUE);
		}
		
		@PlanBody
		protected IFuture<Void> body()
		{
			System.out.println("Plan D");
			res += "D";
			return IFuture.DONE;
		}
	}
}
