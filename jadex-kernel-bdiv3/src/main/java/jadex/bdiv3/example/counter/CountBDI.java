package jadex.bdiv3.example.counter;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Agent
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class))
public class CountBDI
{
	@Agent
	protected BDIAgent agent;
	
	@Belief
	private int counter;
	
	@AgentBody
	public void body()
	{
//		agent.dispatchGoalAndWait(new CountGoal(10, 5))
//			.addResultListener(new DefaultResultListener<CountGoal>()
//		{
//			public void resultAvailable(CountGoal goal)
//			{
//				System.out.println("My goal succeeded: "+goal);
//			}
//		});
		
		agent.dispatchGoalAndWait(new CountGoal(5, 10))
			.addResultListener(new DefaultResultListener<CountGoal>()
		{
			public void resultAvailable(CountGoal goal)
			{
				System.out.println("My goal succeeded: "+goal);
			}
		});
		
		System.out.println("body end: "+getClass().getName());
	}
	
	@Plan(trigger=@Trigger(goals=CountGoal.class))
	protected IFuture<Void> inc(CountGoal goal)
	{
		counter++;
		System.out.println("counter is: "+counter);
		return IFuture.DONE;
	}
}
