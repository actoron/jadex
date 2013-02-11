package jadex.bdiv3.testcases.semiautomatic;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

@Agent
public class PlanPreconditionBDI
{
	/** The agent. */
	@Agent
	protected BDIAgent agent;
	
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
			return new Future<Void>(new PlanFailureException());
//			return IFuture.DONE;
		}
	}
	
	@Plan(trigger=@Trigger(goals=SomeGoal.class))
	protected class PlanB
	{
		@PlanPrecondition
		protected IFuture<Boolean> precondition()
		{
			return new Future<Boolean>(Boolean.FALSE);
		}
		
		@PlanBody
		protected IFuture<Void> body()
		{
			System.out.println("Plan B");
			return IFuture.DONE;
		}
	}
	
	@Plan(trigger=@Trigger(goals=SomeGoal.class))
	protected class PlanC
	{
		@PlanPrecondition
		protected IFuture<Boolean> precondition()
		{
			return new Future<Boolean>(Boolean.TRUE);
		}
		
		@PlanBody
		protected IFuture<Void> body()
		{
			System.out.println("Plan C");
			return IFuture.DONE;
		}
	}
}
