package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Test plan priorities.
 */
public class PlanPrioritiesTesterPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		TestReport tr = new TestReport("#1", "Test plan priorities.");
		IGoal goal = createGoal("test");
		dispatchSubgoalAndWait(goal);

		Integer[] results = (Integer[])getBeliefbase().getBeliefSet("results").getFacts();
		boolean succ = results.length==3;
		for(int i=0; succ && i<results.length-1; i++)
		{
			succ = results[i].intValue()>=results[i+1].intValue();
		}
		if(succ)
			tr.setSucceeded(true);
		else
			tr.setReason("Plan priorities were not respected.");

		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
