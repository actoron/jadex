package jadex.bdiv3.testcases.semiautomatic;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanContextCondition;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

@Agent
public class PlanContextConditionBDI
{
	/** The agent. */
	@Agent
	protected BDIAgent agent;
	
	/** The counter belief. */
	@Belief
	private int counter;
	
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
		agent.dispatchTopLevelGoal(new SomeGoal()).addResultListener(new IResultListener<Object>()
		{
			public void resultAvailable(Object result)
			{
				System.out.println("succ: "+result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
		});
		
		agent.waitFor(500, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				incCounter();
				return IFuture.DONE;
			}
		});
	}
	
	@Plan(trigger=@Trigger(goals=SomeGoal.class))
	protected class PlanA
	{
		@PlanContextCondition(events="counter")
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
	}
	
	/**
	 * 
	 */
	protected void incCounter()
	{
		counter++;
	}
}
