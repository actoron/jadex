package jadex.bdi.testcases.beliefs;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.concurrent.TimeoutException;

/**
 *  Test if adding facts by two different plans is detected
 */
public class WaitForFactAddedPlan extends Plan
{
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void body()
	{
		TestReport tr = new TestReport("#1", "Test waitForFactAdded.");
		int counter = 0;
		int todetect = 5; // adding 5 facts
		getWaitqueue().addFactAdded("beliefSetToAddFacts");
		try
		{
			while(counter < todetect)
			{
				getLogger().info("waiting for facts to be added");
				Object o = waitForFactAdded("beliefSetToAddFacts", 2000);
				getLogger().info("added fact detected: " + o);
				//System.out.println("added fact detected: " + o);
				counter++;
				// Waiting here causes further fact adds in the meantime so that
				// they cannot be detected without waitqueue
				waitFor(100); 
			}
			getLogger().info("Test 1 succeeded.");
			tr.setSucceeded(true);
		}
		catch(TimeoutException e)
		{
			getLogger().info("Test 1 failed.");
			tr.setReason("Not all added facts detected (" + counter + "/" + todetect + ").");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
