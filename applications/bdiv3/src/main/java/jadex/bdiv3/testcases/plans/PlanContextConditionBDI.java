package jadex.bdiv3.testcases.plans;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanContextCondition;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

@Agent(type=BDIAgentFactory.TYPE)
@Results(@Result(name="testresults", clazz=Testcase.class))
public class PlanContextConditionBDI
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The counter belief. */
	@Belief
	private int counter;
	
	@Goal
	protected class SomeGoal
	{
	}
	
	boolean aborted = false;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		TestReport tr = new TestReport("#1", "Test if context condition works.");
		
		agent.getFeature(IExecutionFeature.class).waitForDelay(500, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				counter++;
//				incCounter();
				return IFuture.DONE;
			}
		});
				
		try
		{
			agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new SomeGoal()).get();
		}
		catch(Exception e)
		{
			if(aborted)
			{
				tr.setSucceeded(true);
			}
			else
			{
				tr.setFailed("Plan was not aborted due to invalid context.");
			}
		}
		
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
		agent.killComponent();
	}
	
	@Plan(trigger=@Trigger(goals=SomeGoal.class))
	protected class PlanA
	{
		@PlanContextCondition//(beliefs="counter")
		protected boolean contextcondition()
		{
			return counter==0;
		}
		
		@PlanBody
		protected IFuture<Void> body(IPlan plan)
		{
			final Future<Void> ret = new Future<Void>();
			System.out.println("Plan A start");
			
			plan.waitFor(1000).addResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
					System.out.println("Plan A end");
					ret.setResult(null);
				}
			});
			
			return ret;
		}
		
		@PlanAborted
		protected void aborted()
		{
			aborted = true;
			System.out.println("aborted: "+this);
		}
	}
	
//	/**
//	 * 
//	 */
//	protected void incCounter()
//	{
//		counter++;
//	}
}
