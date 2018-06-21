package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Test if trigger conditions of plans work.
 */
public class PlanConditionTesterPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		TestReport tr = new TestReport("#1", "Test if a condition triggers a plan.");
		int a1 = ((Integer)getBeliefbase().getBelief("a").getFact()).intValue();
		getBeliefbase().getBelief("count").setFact(Integer.valueOf(1));
		waitFor(100);
		int a2 = ((Integer)getBeliefbase().getBelief("a").getFact()).intValue();
		if(a1+1==a2)
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("No plan was triggered.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#2", "Test if a condition triggers a plan with binding.");
		a1 = ((Integer)getBeliefbase().getBelief("a").getFact()).intValue();
		getBeliefbase().getBelief("count").setFact(Integer.valueOf(2));
		waitFor(1000);
		a2 = ((Integer)getBeliefbase().getBelief("a").getFact()).intValue();
		if(a1+2==a2)
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("No plan was triggered.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
