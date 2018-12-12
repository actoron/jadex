package jadex.bdiv3.testcases.semiautomatic;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 *  Simple agent with inline count goal.
 */
@Agent(type=BDIAgentFactory.TYPE)
//@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class))
public class CountBDI
{
//	static
//	{
//		System.out.println("countbdi: "+CountBDI.class.getClassLoader());
//	}
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The counter belief. */
	@Belief
	private int counter;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		IFuture<CountGoal> fut = agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new CountGoal(10, 5));
		fut.addResultListener(new A());
		
//		agent.dispatchGoalAndWait(new CountGoal(10, 5))
//			.addResultListener(new IResultListener<CountGoal>()
//		{
//			public void resultAvailable(CountGoal goal)
//			{
//				System.out.println("My goal succeeded: "+goal);
//			}
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("My goal failed: "+exception);
//			}
//		});
//		
//		agent.dispatchGoalAndWait(new CountGoal(5, 10))
//			.addResultListener(new DefaultResultListener<CountGoal>()
//		{
//			public void resultAvailable(CountGoal goal)
//			{
//				System.out.println("My goal succeeded: "+goal);
//			}
//		});
		
		System.out.println("body end: "+getClass().getName());
	}
	
	/**
	 *  Inline plan method that reacts on count goal.
	 */
	@Plan(trigger=@Trigger(goals=CountGoal.class))
	protected IFuture<Void> inc(CountGoal goal)
	{
		counter++;
		System.out.println("counter is: "+counter);
		return IFuture.DONE;
	}
	
	static class A implements IResultListener<CountGoal>
	{
		static
		{
			System.out.println("A classloader: "+A.class.getClassLoader());
		}
		
		public void resultAvailable(CountGoal goal)
		{
			System.out.println("My goal succeeded: "+goal);
		}
		public void exceptionOccurred(Exception exception)
		{
			System.out.println("My goal failed: "+exception);
		}
	}
}
