package jadex.bdiv3.testcases.semiautomatic;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

@Agent
public class ABDI extends AABDI
{
	@Agent
	protected BDIAgent agent;
	
	@Belief
	protected int num2;

	@Goal(recur=true)
	public class Cnt1Goal
	{
		@GoalTargetCondition(events="num1")
		protected boolean checkTarget()
		{
			return num1==2;
		}
	}
	
	@Goal(recur=true)
	public class Cnt2Goal
	{
		@GoalTargetCondition(events="num2")
		protected boolean checkTarget()
		{
			return num2==3;
		}
	}
	
	@AgentBody
	public IFuture<Void> body()
	{
		agent.dispatchTopLevelGoal(new Cnt1Goal())
			.addResultListener(new IResultListener<ABDI.Cnt1Goal>()
		{
			public void resultAvailable(Cnt1Goal result)
			{
				System.out.println("fulfilled1: "+result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
		});
		
		agent.dispatchTopLevelGoal(new Cnt2Goal())
			.addResultListener(new IResultListener<ABDI.Cnt2Goal>()
		{
			public void resultAvailable(Cnt2Goal result)
			{
				System.out.println("fulfilled2: "+result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
		});
		
		IComponentStep<Void> step = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				incNum1();
				incNum2();
				
				agent.waitForDelay(1000, this);
				
				return IFuture.DONE;
			}
		};
		
		agent.waitForDelay(1000, step);
		
		return new Future<Void>();
	}
	
	/**
	 *  Get the num2.
	 *  @return The num2.
	 */
	public int getNum2()
	{
		return num2;
	}

	/**
	 *  Set the num2.
	 *  @param num2 The num2 to set.
	 */
	public void setNum2(int num2)
	{
		this.num2 = num2;
	}
	
	/**
	 * 
	 */
	public void incNum2()
	{
		this.num2++;
		System.out.println("num2: "+num2);
	}
}
