package jadex.bdiv3.examples.moneypainter;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.ServicePlan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.examples.moneypainter.RichAgent.GetOneEuro;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.OnStart;
import jadex.commons.future.Future;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * 
 */
@Agent(type=BDIAgentFactory.TYPE)
@RequiredServices(@RequiredService(name="getser", type=IPaintMoneyService.class, scope=ServiceScope.PLATFORM))
@Plans(@Plan(trigger=@Trigger(goals=GetOneEuro.class), body=@Body(service=@ServicePlan(name="getser"))))
public class RichAgent
{
	@Agent
	protected IInternalAccess agent;
	
	/** The target amount of money. */
	@Belief
	protected int target = 3;
	
	/** The money. */
	@Belief
	protected int money;
	
	//@AgentBody
	@OnStart
	public void body()
	{
		agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new BecomeRich()).get();
		if(money==target)
		{
			System.out.println("Now I am rich as I have made "+money+" euros.");
		}
		else
		{
			System.out.println("I have made only "+money+" euros, planned were "+target);
		}
	}
	
	@Goal
	public class BecomeRich
	{
		@GoalTargetCondition//(beliefs="money")
		public boolean checkTarget()
		{
			return money==target;
		}
	}
	
	@Goal(excludemode=ExcludeMode.Never, retrydelay=1000)
	//@Goal(recur=true, recurdelay=1000)
	public class GetOneEuro
	{
		@GoalParameter
		protected String agent;
		
		public GetOneEuro(String agent)
		{
			this.agent = agent;
		}
	}
	
	@Plan(trigger=@Trigger(goals=BecomeRich.class))
	public void distributeWork(IPlan plan)
	{
		// Create a subgoal for each euro to get
		final Future<Void> fut = new Future<Void>();
		final int max = target-money;
		IResultListener<Object> lis = new IResultListener<Object>()
		{
			int cnt = 0;
			public void resultAvailable(Object result)
			{
				System.out.println("Get money goal success: "+result);
				money++;
//				incMoney();
				proceed();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("Get money goal failed: "+exception);
				proceed();
			}
			
			protected void proceed()
			{
				System.out.println("cnt,max:"+cnt+" "+max);
				if(++cnt==max)
				{
					fut.setResult(null);
				}
			}
		};
		for(int i=0; i<max; i++)
		{
			GetOneEuro goal = new GetOneEuro(agent.getId().getLocalName());
			plan.dispatchSubgoal(goal).addResultListener(lis);
		}
		fut.get();
		//System.out.println("distribute work fini: "+money+" "+target);
	}
	
	protected void incMoney()
	{
		money++;
	}
	
/*	@Plan(trigger=@Trigger(goalfinisheds=BecomeRich.class))
//	public void printRich(BecomeRich goal) // Injection works but cannot access RGoal from pojo at this point
	public void printRich(IGoal goal)
	{
		System.out.println("Become rich finished: "+goal);
		
//		if(agent.getComponentFeature(IBDIAgentFeature.class).getGoal(goal).isSucceeded())
		if(goal.isSucceeded())
		{
			System.out.println("Now I am rich as I have made "+money+" euros.");
		}
		else
		{
			System.out.println("I have made only "+money+" euros, planned were "+target);
		}
	}*/
}

