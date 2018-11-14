package jadex.bdiv3.testcases.misc;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test using injected values in init expressions or constructors.
 */
@Agent(type=BDIAgentFactory.TYPE, keepalive=Boolean3.FALSE)
@Results(@Result(name="testresults", clazz=Testcase.class))
public class PlanParameterMappingBDI
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;

	@Goal
	public class AGoal
	{
		@GoalParameter
		@GoalResult
		protected String p;

		/**
		 *  Create a goal.
		 */
		public AGoal(String p)
		{
			this.p = p;
		}

		/**
		 *  Get the p.
		 *  @return The p.
		 */
		public String getP()
		{
			return p;
		}

		/**
		 *  Set the p.
		 *  @param p The p to set.
		 */
		public void setP(String p)
		{
			this.p = p;
		}
	}
	
	//-------- methods --------

	/**
	 * 
	 */
	@Plan(trigger=@Trigger(goals=AGoal.class))
	protected String doubleP(String p)
	{
		return p+p;
	}
	
	/**
	 *  Agent body.
	 */
	@AgentBody//(keepalive=false)
	public void	body()
	{
		String res = (String)agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new AGoal("hello")).get();
		System.out.println("res: "+res);
		
		TestReport tr = new TestReport("#1", "Test if goal plan parameter mappings work.");
		if("hellohello".equals(res))
		{			
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Wrong mapping, "+res);
		}
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
	}
}
