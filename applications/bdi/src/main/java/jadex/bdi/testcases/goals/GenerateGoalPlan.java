package jadex.bdi.testcases.goals;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Create a goal and wait for the result.
 */
public class GenerateGoalPlan extends Plan
{
	public void body()
	{
		TestReport	report	= new TestReport("test_goal", "Dispatch a goal and wait for the result");
		try
		{
			IGoal	agoal	= createGoal("testgoal");
			
			// If "param" is present set to "foo".
			if(agoal.hasParameter("param"))
			{
				agoal.getParameter("param").setValue("foo");
			}

			// If "params" is present set to {"foo", "bar"}.
			if(agoal.hasParameterSet("params"))
			{
				agoal.getParameterSet("params").addValue("foo");
				agoal.getParameterSet("params").addValue("bar");
			}

			dispatchSubgoalAndWait(agoal);

			// If "result" is present check for "foo".
			if(!agoal.hasParameter("result") || "foo".equals(agoal.getParameter("result").getValue()))
				report.setSucceeded(true);
			else
				report.setReason("Wrong result value: "+agoal.getParameter("result").getValue());
		}
		catch(GoalFailureException gfe)
		{
			report.setReason(gfe.toString());
		}
		getBeliefbase().getBeliefSet("reports").addFact(report);
	}
}
