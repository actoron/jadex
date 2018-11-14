package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;

/**
 * 
 */
public class ToStartPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		System.out.println("started: "+this);
		
		TestReport tr = new TestReport("#1", "Test if plan can be started manually.");
		tr.setSucceeded(true);
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
