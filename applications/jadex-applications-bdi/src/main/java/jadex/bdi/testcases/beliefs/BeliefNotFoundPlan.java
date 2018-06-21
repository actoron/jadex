package jadex.bdi.testcases.beliefs;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Test belief access.
 */
public class BeliefNotFoundPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		TestReport tr = new TestReport("#1", "Test belief access.");
		try
		{
			getBeliefbase().getBelief("belx");
			tr.setReason("No exception occurred");
		}
		catch(Exception e)
		{
			tr.setSucceeded(true);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		tr = new TestReport("#1", "Test beliefset access.");
		try
		{
			getBeliefbase().getBeliefSet("belsetx");
		}
		catch(Exception e)
		{
			tr.setSucceeded(true);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}

