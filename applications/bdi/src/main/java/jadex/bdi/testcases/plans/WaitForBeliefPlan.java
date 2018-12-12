package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Wait endlessly for belief changes.
 */
public class WaitForBeliefPlan extends Plan
{
	//-------- attributes --------

	/** The test report. */
	protected TestReport tr;

	//-------- constructors --------

	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		for(int i=0; i<3; i++)
		{
			tr = new TestReport("#"+i, "Tests if a beliefchange is detected.");
			getLogger().info("Now waiting for a change of belief 'some_number': ");
			waitForFactChanged("some_number", 3000);
			tr.setSucceeded(true);
			getLogger().info("Belief changed: "+getBeliefbase().getBelief("some_number").getFact());
			getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		}
	}

	/**
	 *  Called on plan failure.
	 */
	public void failed()
	{
		getLogger().info("No belief update detected. Plan failed.");
		if(tr==null)
			tr = new TestReport("", "Plan failure occurred before test was created.");
		tr.setReason("No belief update detected");
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
