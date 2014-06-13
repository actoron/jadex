package jadex.bdiv3.testcases.goals;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.util.ArrayList;
import java.util.List;

/**
 *  Test if a procedural goal can be configured with 
 *  OR and AND success.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class SequentialGoalBDI
{
	/** The bdi agent. */
	@Agent
	protected BDIAgent agent;
		
	/**
	 *  Procedural goal (no target condition) with
	 *  AND success turned on. All plans have to be
	 *  executed and one has to be passed.
	 */
	@Goal(orsuccess=false)
	public class TestGoal
	{
		@GoalParameter
		protected List<String> results = new ArrayList<String>();
		
		@GoalResult
		protected void resultReceived(String res)
		{
//			System.out.println("rec: "+res);
			results.add(res);
		}
		
		@GoalResult
		protected List<String> getResult()
		{
			return results;
		}
	}
	
	@Plan(trigger=@Trigger(goals=TestGoal.class))
	public String p1(IPlan plan)
	{
		plan.waitFor(200).get();
		return "p1";
	}
	
	@Plan(trigger=@Trigger(goals=TestGoal.class))
	public String p2(IPlan plan)
	{
		plan.waitFor(200).get();
		return "p2";
	}
	
	@Plan(trigger=@Trigger(goals=TestGoal.class))
	public String p3(IPlan plan)
	{
		plan.waitFor(200).get();
		return "p3";
	}
	
	@AgentBody
	public void body()
	{
		final TestReport tr = new TestReport("#1", "Test if a goal with AND success for plans work.");
		
		agent.waitForDelay(2000, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(!tr.isFinished())
				{
					tr.setFailed("Goal did return");
					agent.setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
				}
				
				agent.killAgent();
				return IFuture.DONE;
			}
		});
		
		Object res = agent.dispatchTopLevelGoal(new TestGoal()).get();
		System.out.println("Goal success: "+res);
		tr.setSucceeded(true);
		agent.setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
		agent.killAgent();
	}
}
