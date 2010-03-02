package jadex.bdi.testcases.beliefs;

import jadex.base.test.TestReport;
import jadex.bdi.runtime.Plan;

/**
 *  Test if dynamic beliefs work.
 */
public class DynamicBeliefPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		TestReport tr = new TestReport("#1", "Change belief string to some value and see how the length belief changes.");
		getLogger().info("Test 1: Test if dependent belief works.");
		getBeliefbase().getBelief("string").setFact("test");
		Integer length = (Integer)getBeliefbase().getBelief("length").getFact();
		if(length!=null && length.intValue()==4)
		{
			tr.setSucceeded(true);
			getLogger().info("Test 1: Succeeded.");
		}
		else
		{
			tr.setReason("Length is wrong.");
			getLogger().info("Test 1: Failed.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
