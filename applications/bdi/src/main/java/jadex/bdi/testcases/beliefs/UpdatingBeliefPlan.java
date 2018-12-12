package jadex.bdi.testcases.beliefs;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.TimeoutException;

/**
 *  Test if auto updating beliefs work.
 */
public class UpdatingBeliefPlan extends Plan
{
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void body()
	{
		waitFor(100);
		TestReport tr = new TestReport("#1", "Test belief auto update.");
		try
		{
			waitForFactChanged("time", 1000);
			getLogger().info("Test 1 succeeded.");
			tr.setSucceeded(true);
		}
		catch(TimeoutException e)
		{
			getLogger().info("Test 1 failed.");
			tr.setReason("No belief update detected.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
