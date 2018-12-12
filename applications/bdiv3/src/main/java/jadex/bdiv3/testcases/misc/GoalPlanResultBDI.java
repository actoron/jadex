package jadex.bdiv3.testcases.misc;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Goal;
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
 *  Agent that has a plan that return a value form the body.
 *  The value is then used as result value of the goal when the goal uses the @GoalResult.
 */
@Agent(type=BDIAgentFactory.TYPE, keepalive=Boolean3.FALSE)
@Results(@Result(name="testresults", clazz=Testcase.class))
public class GoalPlanResultBDI
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	@Goal
	public class AGoal
	{
		@GoalResult
		protected String result;
	}
	
	@Goal
	public class BGoal
	{
		protected String result;

		/**
		 *  Get the result.
		 *  @return The result.
		 */
		@GoalResult
		public String getResult()
		{
			return result;
		}

		/**
		 *  Set the result.
		 *  @param result The result to set.
		 */
		@GoalResult
		public void setResult(String result)
		{
			this.result = result;
		}
	}
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		String res1 = (String)agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new AGoal()).get();
		TestReport	tr1	= new TestReport("#1", "Test if goal result set/get works with field.");
		if("result".equals(res1))
		{			
			tr1.setSucceeded(true);
		}
		else
		{
			tr1.setReason("Result not received "+res1);
		}
		String res2 = (String)agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new BGoal()).get();
		
		
		TestReport	tr2	= new TestReport("#1", "Test if goal result set/get works with method.");
		if("result".equals(res1))
		{			
			tr2.setSucceeded(true);
		}
		else
		{
			tr2.setReason("Result not received "+res1);
		}
		
		System.out.println(res1+" "+res2);
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(2, new TestReport[]{tr1, tr2}));
	}
	
	/**
	 *  Plan that reacts on the goals.
	 */
	@Plan(trigger=@Trigger(goals={AGoal.class, BGoal.class}))
	public String plan()
	{
		return "result";
	}
}
