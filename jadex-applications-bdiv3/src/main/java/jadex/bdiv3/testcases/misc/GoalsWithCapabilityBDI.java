package jadex.bdiv3.testcases.misc;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.testcases.misc.TestCapability.TestGoal;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test if plans within a capability get executed on goal dispatch.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class GoalsWithCapabilityBDI
{
	/** The agent. */
	@Agent
	protected BDIAgent	agent;
	
	@Capability
	protected TestCapability testcap = new TestCapability();
	
	/**
	 *  Agent body.
	 */
	@AgentBody
	public void	body(final BDIAgent agent)
	{
		final TestReport tr	= new TestReport("#1", "Test if capability goals work.");
		
		TestGoal goal = testcap.new TestGoal();
		agent.dispatchTopLevelGoal(goal).get();
		
		if(goal.getCnt()==2)
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Wrong number of plans executed: "+goal.getCnt());
		}
		
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
