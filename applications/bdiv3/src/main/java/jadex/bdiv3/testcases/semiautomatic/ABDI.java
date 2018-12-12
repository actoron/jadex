package jadex.bdiv3.testcases.semiautomatic;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

@Agent(type=BDIAgentFactory.TYPE)
public class ABDI extends AABDI
{
//	static
//	{
//		System.out.println("abdi1: "+ABDI.class.hashCode()+" "+ABDI.class.getClassLoader());
//		System.out.println("aabdi1: "+AABDI.class.hashCode()+" "+AABDI.class.getClassLoader());
//	}
	
	@Agent
	protected IInternalAccess agent;
	
	@Belief
	protected int num2;

	@Goal(recur=true)
	public class Cnt1Goal
	{
		@GoalTargetCondition//(beliefs="num1")
		protected boolean checkTarget()
		{
			return num1==2;
		}
	}
	
	@Goal(recur=true)
	public class Cnt2Goal
	{
		@GoalTargetCondition//(beliefs="num2")
		protected boolean checkTarget()
		{
			return num2==3;
		}
	}
	
	@AgentBody
	public IFuture<Void> body()
	{
		IFuture<Cnt1Goal> fut1 = agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new Cnt1Goal());
		fut1.addResultListener(new IResultListener<ABDI.Cnt1Goal>()
		{
			public void resultAvailable(Cnt1Goal result)
			{
				System.out.println("fulfilled1: "+result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("failed: "+exception);
			}
		});
		
		IFuture<Cnt2Goal> fut2 = agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new Cnt2Goal());
		fut2.addResultListener(new IResultListener<ABDI.Cnt2Goal>()
		{
			public void resultAvailable(Cnt2Goal result)
			{
				System.out.println("fulfilled2: "+result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("failed: "+exception);
			}
		});
		
		IComponentStep<Void> step = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				incNum1();
				incNum2();
				
				agent.getFeature(IExecutionFeature.class).waitForDelay(1000, this);
				
				return IFuture.DONE;
			}
		};
		
		agent.getFeature(IExecutionFeature.class).waitForDelay(1000, step);
		
		try
		{
			fut1.get();
			fut2.get();
		}
		catch (Exception e)
		{
		}
		
		return IFuture.DONE;
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
