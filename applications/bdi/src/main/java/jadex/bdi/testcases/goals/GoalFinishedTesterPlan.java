package jadex.bdi.testcases.goals;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Test if a plan can be activated in response
 *  to a goal finished event.
 */
public class GoalFinishedTesterPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		TestReport tr = new TestReport("#1", "Test if a plan can be activated via a goal finished event");
		IGoal goal = createGoal("testgoal");
		//dispatchSubgoal(goal);
		dispatchSubgoalAndWait(goal);
		
		waitFor(200);
		if(((Boolean)getBeliefbase().getBelief("result").getFact()).booleanValue())
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setFailed("Plan was not activated in response to a goal finished event");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
