package jadex.bdi.testcases.plans;

import java.util.Arrays;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Wait endlessly for belief changes.
 */
public class WaitForBeliefSetPlan extends Plan
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
			getLogger().info("Now waiting for a change of belief set 'some_numbers': ");
			Object val = waitForFactAdded("some_numbers", 3000);
			getLogger().info("Fact added: "+val);
			//Object val = waitForFactRemoved("some_numbers", 5000);
			//getLogger().info("Fact removed: "+val);
			tr.setSucceeded(true);
			getLogger().info("Belief changed: "+Arrays.toString(getBeliefbase().getBeliefSet("some_numbers").getFacts()));
			getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		}
	}

	/**
	 *  Called on plan failure.
	 */
	public void failed()
	{
		getLogger().info("No belief set update detected. Plan failed.");
		if(tr==null)
			tr = new TestReport("", "Plan failure occurred before test was created.");
		tr.setReason("No belief set update detected");
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}

}

