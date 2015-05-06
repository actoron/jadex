package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Testing waiting for sync results.
 */
public class SyncResultListenerTestPlan extends Plan
{
	/**
	 *  The body method.
	 */
	public void body()
	{
		TestReport	tr = new TestReport("#1", "Testing waitForResult().");
		SyncResultListener	listener	= new SyncResultListener();
		getBeliefbase().getBelief("listener").setFact(listener);
		Object	result	= listener.waitForResult();
		if("success".equals(result))
			tr.setSucceeded(true);
		else
			tr.setFailed("Wrong result received: "+result);
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
