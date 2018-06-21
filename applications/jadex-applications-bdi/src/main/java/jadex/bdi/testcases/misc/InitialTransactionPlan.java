package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Test if initial transaction works. 
 */
public class InitialTransactionPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		waitFor(100);
		TestReport tr = new TestReport("#1", "Test if plan was executed.");
		if(getBeliefbase().getBelief("result").getFact().equals("Hello World!"))
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Plan was not triggered.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
