package jadex.bdiv3.testcases.componentplans;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.IBDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentResult;

/**
 *  BDI agent that executes a subcomponent as plan
 */
@Agent(type=BDIAgentFactory.TYPE, keepalive=Boolean3.FALSE)
@Plans(@Plan(trigger=@Trigger(goals=ComponentPlanBDI.AchieveSuccess.class),
	body=@Body(ComponentPlanAgent.class)))
//@Results(@Result(name="testresults", clazz=Testcase.class))
public abstract class ComponentPlanBDI implements IBDIAgent
{
	//-------- attributes --------
	
	/** The success belief. */
	@Belief
	protected boolean	success;
	
	@AgentResult
	protected Testcase	testresults;
	
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
	@AgentBody
	public void	body(IInternalAccess agent)
	{
		TestReport	tr	= new TestReport("#1", "Test if goal can be achieved by component plan.");
		try
		{
			agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new AchieveSuccess()).get(5000);
			tr.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr.setFailed(e);
		}
		
		testresults	= new Testcase(1, new TestReport[]{tr});
	}
	
	//-------- goals --------
	
	@Goal
	public class AchieveSuccess
	{
		@GoalTargetCondition//(beliefs="success")
		public boolean	achieved()
		{
			return success;
		}
	}
}
