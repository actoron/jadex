package jadex.bdiv3.testcases.semiautomatic;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.OnStart;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;

//import java.beans.PropertyChangeListener;
//import java.beans.PropertyChangeSupport;

@Agent(type=BDIAgentFactory.TYPE)
@Configurations({@Configuration(name="1"), @Configuration(name="2")})
public class BasicTypeConditionBDI 
{	
	@Agent
	protected IInternalAccess agent;
	
	@Belief 
	protected long mybel = 0;
	
	@OnStart
	public void body()
	{
		agent.waitForDelay(2000, ia ->
		{
			agent.getFeature(IBDIAgentFeature.class).adoptPlan("notify");
			return IFuture.DONE;
		});
		
		if("1".equals(agent.getConfiguration()))
		{
			agent.getFeature(IBDIAgentFeature.class).adoptPlan("wait");
		}
	}
	
	@Plan
	protected void wait(IPlan plan)
	{
		System.out.println("waiting for notification");
		plan.waitForFactChanged("mybel").get();
		System.out.println("received notification");
	}
	
	@Plan
	protected void notify(IPlan plan)
	{
		System.out.println("notify using bean");
		mybel = 1;
	}
}