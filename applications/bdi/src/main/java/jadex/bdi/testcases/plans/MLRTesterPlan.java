package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Test the meta-level reasoning in combination with retry.
 */
public class MLRTesterPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		while(getGoalbase().getGoals("app_goal").length!=0)
			waitFor(100);

		TestReport tr = new TestReport("#1", "Test if meta-level reasoning works with retry.");
		if(((Integer)getBeliefbase().getBelief("result").getFact()).intValue()==2)
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Not 2 plans have been executed.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
