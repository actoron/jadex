package jadex.bdiv3.testcases.misc;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.testcases.misc.TestCapability.TestGoal;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test if plans within a capability get executed on goal dispatch.
 */
@Agent(type=BDIAgentFactory.TYPE, keepalive=Boolean3.FALSE)
@Results(@Result(name="testresults", clazz=Testcase.class))
public class GoalsWithCapabilityBDI
{
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	@Capability
	protected TestCapability testcap = new TestCapability();
	
	/**
	 *  Agent body.
	 */
	@AgentBody//(keepalive=false)
	public void	body(final IInternalAccess agent)
	{
		final TestReport tr	= new TestReport("#1", "Test if capability goals work.");
		
		TestGoal goal = testcap.new TestGoal();
		agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(goal).get(3000);
		
		if(goal.getCnt()==2)
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Wrong number of plans executed: "+goal.getCnt());
		}
		
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
	}
	
	/**
	 *  Plan in agent.
	 */
	@Plan(priority=-1, trigger=@Trigger(goals=TestGoal.class))
	public void agentPlan(TestGoal goal)
	{
		goal.setCnt(goal.getCnt()+1);
		System.out.println("Agent plan.");
	}
}
