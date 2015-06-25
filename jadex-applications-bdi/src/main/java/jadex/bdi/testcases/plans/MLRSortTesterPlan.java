package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.IInternalEvent;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Test the meta-level reasoning in combination with retry.
 */
public class MLRSortTesterPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		TestReport tr = new TestReport("#1", "Test if meta-level reasoning works for goals.");
		IGoal ag = createGoal("mlrcap.app_goal");
		try
		{
			dispatchSubgoalAndWait(ag);
		}
		catch(GoalFailureException e)
		{
		}
		validateTestResult(tr);

		tr = new TestReport("#2", "Test if meta-level reasoning works for internal events.");
		IInternalEvent tie	= createInternalEvent("mlrcap.testinternalevent");
		dispatchInternalEvent(tie);
		waitFor(100);	// Wait until plans are executed.
		validateTestResult(tr);

		/*tr = new TestReport("#3", "Test if meta-level reasoning works for filters.");
		IInternalEvent tf	= createInternalEvent("mlrcap.testfilter");
		dispatchInternalEvent(tf);
		waitFor(100);	// Wait until plans are executed.
		validateTestResult(tr);*/

		// Todo: test messages (how? is posted to one without retry).
		// Todo: test goal finished.
	}

	/**
	 *  Check if the meta-level reasoning was successful.
	 */
	protected void validateTestResult(TestReport tr)
	{
		Double[] plans = (Double[])getBeliefbase().getBeliefSet("mlrcap.plans").getFacts();
		boolean succ = plans.length>0;
		for(int i=0; succ && i<plans.length-1; i++)
		{
			succ = plans[i].doubleValue()>=plans[i+1].doubleValue();
		}

		if(succ)
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Meta-level reasoning error.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		getBeliefbase().getBeliefSet("mlrcap.plans").removeFacts();
	}
}
