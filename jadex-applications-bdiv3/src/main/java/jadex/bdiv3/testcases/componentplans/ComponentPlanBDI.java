package jadex.bdiv3.testcases.componentplans;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  BDI agent that executes a subcomponent as plan
 */
@Agent
@Plans(@Plan(trigger=@Trigger(goals=ComponentPlanBDI.AchieveSuccess.class),
	body=@Body(ComponentPlanAgent.class)))
@Results(@Result(name="testresults", clazz=Testcase.class))
public class ComponentPlanBDI
{
	//-------- attributes --------
	
	/** The success belief. */
	@Belief
	protected boolean	success;
	
	//-------- methods --------
	
	/**
	 *  Set the success belief.
	 */
	public void	setSuccess(boolean success)
	{
		this.success	= success;
	}
	
	/**
	 *  Agent body.
	 */
	@AgentBody(keepalive=false)
	public void	body(BDIAgent agent)
	{
		TestReport	tr	= new TestReport("#1", "Test if goal can be achieved by component plan.");
		try
		{
			agent.dispatchTopLevelGoal(new AchieveSuccess()).get(500);
			tr.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr.setFailed(e);
		}
		
		agent.setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
	}
	
	//-------- goals --------
	
	@Goal
	public class AchieveSuccess
	{
		@GoalTargetCondition(beliefs="success")
		public boolean	achieved()
		{
			return success;
		}
	}
}
