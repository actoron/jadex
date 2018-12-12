package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Test a plan precondition.
 */
public class PreconditionTesterPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		TestReport tr = new TestReport("#1", "Test plan precondition.");
		IGoal goal = createGoal("test");
		dispatchSubgoalAndWait(goal);

		int result = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		if(result==2)
			tr.setSucceeded(true);
		else
			tr.setReason("Wrong plan were chosen.");

		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
