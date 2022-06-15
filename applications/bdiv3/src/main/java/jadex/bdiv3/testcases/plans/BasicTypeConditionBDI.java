package jadex.bdiv3.testcases.plans;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.annotation.OnEnd;
import jadex.bridge.service.annotation.OnStart;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

@Agent(type=BDIAgentFactory.TYPE)
@Plans(
{
	@Plan(body=@Body(BasicTypeWaitPlan.class)),
	@Plan(body=@Body(BasicTypeNotifyPlan.class))
})
@Results(@Result(name="testresults", clazz=Testcase.class))
public class BasicTypeConditionBDI 
{	
	@Agent
	protected IInternalAccess agent;
	
	@Belief 
	protected long mybel = 0;
	
	protected TestReport[] tr = new TestReport[1];
	
	@OnStart
	public void body()
	{
		tr[0] = new TestReport("#1", "Test waitForFactChanged");
		
		agent.waitForDelay(4000, ia ->
		{
			agent.killComponent();
			return IFuture.DONE;
		});
		
		agent.waitForDelay(2000, ia ->
		{
			agent.getFeature(IBDIAgentFeature.class).adoptPlan("jadex.bdiv3.testcases.plans.BasicTypeNotifyPlan");
			return IFuture.DONE;
		});
		
		agent.getFeature(IBDIAgentFeature.class).adoptPlan("jadex.bdiv3.testcases.plans.BasicTypeWaitPlan").get();
		tr[0].setSucceeded(true);
		agent.killComponent();
	}
	
	/*@Plan
	protected void wait(IPlan plan)
	{
		System.out.println("waiting for notification");
		//plan.waitForFactChanged("mybel").get();
		plan.waitForBeliefChanged("mybel").get();
		//((RPlan)plan).waitForFactX("mybel", new String[]{ChangeEvent.BELIEFCHANGED}, -1, null).get();
		System.out.println("received notification");
		agent.killComponent().get();
	}*/
	
	//@Plan
	protected void notify(IPlan plan)
	{
		System.out.println("notify using bean");
		mybel = 1;
	}
	
	/**
	 *  Called when agent is killed.
	 */
	@OnEnd
	public void	destroy(IInternalAccess agent)
	{
		for(TestReport ter: tr)
		{
			if(!ter.isFinished())
				ter.setFailed("Plan not activated");
		}
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(tr.length, tr));
	}
}