package jadex.bdi.testcases.beliefs;

import java.util.Date;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Test the expression evaluation modes.
 */
public class EvaluationmodesPlan extends Plan
{
//	//-------- constructors --------
//
//	/**
//	 *  Create a new countdown plan.
//	 */
//	public EvaluationmodesPlan()
//	{
//		getLogger().info("Created: "+this);
//	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		TestReport tr = new TestReport("#1", "Test belief evaluation modes.");
		waitFor(300);
		getLogger().info("Initial dates are: ");
		Date d1a = (Date)getBeliefbase().getBelief("date1").getFact();
		Date d2a = (Date)getBeliefbase().getBelief("date2").getFact();
		getLogger().info("Date1: "+d1a);
		getLogger().info("Date2: "+d2a);

		waitFor(300);
		getLogger().info("Now dates are (only 2nd should have changed): ");
		Date d1b = (Date)getBeliefbase().getBelief("date1").getFact();
		Date d2b = (Date)getBeliefbase().getBelief("date2").getFact();
		getLogger().info("Date1: "+d1b);
		getLogger().info("Date2: "+d2b);

		if(d1a.equals(d1b) && !d2a.equals(d2b))
		{
			getLogger().info("Test 1 succeeded.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("Test 1 failed.");
			tr.setReason("Evaluation modes do not work correctly.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}

