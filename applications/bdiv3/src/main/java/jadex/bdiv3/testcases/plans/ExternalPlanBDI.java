package jadex.bdiv3.testcases.plans;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.OnEnd;
import jadex.micro.annotation.OnStart;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test if an external plan can define a trigger.
 */
@Agent(type=BDIAgentFactory.TYPE)
@Results(@Result(name="testresults", clazz=Testcase.class))
@Plans(@Plan(body=@Body(ExternalPlan.class)))
public class ExternalPlanBDI
{
	protected TestReport tr = new TestReport("#1", "Test if external plan can declare its own trigger.");
	
	@Agent
	protected IInternalAccess agent;
	
	@Goal
	protected class MyGoal
	{
	}
	
	//@AgentBody
	@OnStart
	protected void body()
	{
		try
		{
			agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new MyGoal()).get();
			tr.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr.setFailed(e);
		}
		agent.killComponent();
	}
	
	/**
	 *  Called when agent is killed.
	 */
	//@AgentKilled
	@OnEnd
	public void	destroy(IInternalAccess agent)
	{
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
	}
}
