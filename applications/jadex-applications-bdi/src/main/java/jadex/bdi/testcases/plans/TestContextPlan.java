package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;

/**
 *  This plan just waits until it is aborted.
 */
public class TestContextPlan extends Plan
{
	private TestReport	tr;

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		tr	= new TestReport("#1", "Tests plan abortion due to invalid context.");
		getLogger().info("Waiting: "+this);
		waitFor(1000);
		getLogger().info("Continued: "+this);
		tr.setReason("Plan was not aborted.");
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}

	/**
	 *  Called when the plan is aborted.
	 */
	public void aborted()
	{
		getLogger().info("Plan aborting...");
		tr.setSucceeded(true);
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
