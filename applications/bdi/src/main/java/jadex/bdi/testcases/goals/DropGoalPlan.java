package jadex.bdi.testcases.goals;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.TimeoutException;

/**
 *  Test waiting for a dropped goal.
 */
public class DropGoalPlan extends Plan
{
	public void body()
	{
		// Dispatch goal.
		IGoal	goal	= createGoal("testgoal");
		dispatchSubgoal(goal);
		
		// Wait a sec. and then drop the goal.
		waitFor(1000);
		goal.drop();
		
		// Wait for goal to be finished and check the result.
		TestReport	report	= new TestReport("drop_wait", "Test waiting for a dropped goal.");
		try
		{
			waitForGoalFinished(goal, 1000);
			report.setSucceeded(true);
		}
		catch(GoalFailureException gfee)
		{
			report.setSucceeded(true);
		}
		catch(TimeoutException te)
		{
			report.setFailed("Goal did not finish.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);		
	}
}
