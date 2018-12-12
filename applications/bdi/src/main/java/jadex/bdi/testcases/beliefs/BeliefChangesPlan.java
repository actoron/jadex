package jadex.bdi.testcases.beliefs;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.SUtil;

/**
 *  Test different kinds of belief changes.
 */
public class BeliefChangesPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		int[] plans_executed	= new int[3];
		
		// Hack?! First initial belief reaction plans should be executed :-(
		waitFor(300);

		TestReport tr= new TestReport("#1", "All plans should trigger on initial belief changes.");
		getLogger().info(tr.getDescription());
		if(checkPlans(1, 1, 1, plans_executed))
			tr.setSucceeded(true);
		else
			tr.setReason("Some plans did not execute correctly (expected 1, 1, 1): "+SUtil.arrayToString(plans_executed));
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr= new TestReport("#2", "Belief value is changed and plan a is sensible to that.");
		getLogger().info(tr.getDescription());
		getBeliefbase().getBelief("bel_a").setFact("aaaa");
		waitFor(300);
		if(checkPlans(1, 0, 0, plans_executed))
			tr.setSucceeded(true);
		else
			tr.setReason("Some plans did not execute correctly (expected 1, 0, 0): "+SUtil.arrayToString(plans_executed));
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#3", "Belief value is set to the same value and no plan should react.");
		getLogger().info(tr.getDescription());
		getBeliefbase().getBelief("bel_a").setFact("aaaa");
		waitFor(300);
		if(checkPlans(0, 0, 0, plans_executed))
			tr.setSucceeded(true);
		else
			tr.setReason("Some plans did not execute correctly (expected 0, 0, 0): "+SUtil.arrayToString(plans_executed));
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#4", "Two dependent beliefs are changed and two plans monitor them.");
		getLogger().info(tr.getDescription());
		getBeliefbase().getBelief("bel_a").setFact("new_value");
		waitFor(300);
		if(checkPlans(1, 1, 0, plans_executed))
			tr.setSucceeded(true);
		else
			tr.setReason("Some plans did not execute correctly (expected 1, 1, 0): "+SUtil.arrayToString(plans_executed));
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#5", "A deep bean change is done and a plan monitors that.");
		getLogger().info(tr.getDescription());
		((TestBean)getBeliefbase().getBelief("bel_c").getFact()).setName("new_name");
		waitFor(300);
		if(checkPlans(0, 0, 1, plans_executed))
			tr.setSucceeded(true);
		else
			tr.setReason("Some plans did not execute correctly (expected 0, 0, 1): "+SUtil.arrayToString(plans_executed));
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}

	/**
	 *  Check that plans have been executed as stated.
	 *  Resets the plan_executed counters for next check.
	 *  @return The plans_executed array contains the observed values after the method returns (for debugging).
	 */
	protected boolean	checkPlans(int plan_a, int plan_b, int plan_c, int[] plans_executed)
	{
		plans_executed[0]	= ((Integer)getBeliefbase().getBelief("plan_a_executed").getFact()).intValue();
		plans_executed[1]	= ((Integer)getBeliefbase().getBelief("plan_b_executed").getFact()).intValue();
		plans_executed[2]	= ((Integer)getBeliefbase().getBelief("plan_c_executed").getFact()).intValue();
		boolean	ret	= plan_a==plans_executed[0] && plan_b==plans_executed[1] && plan_c==plans_executed[2];
		getBeliefbase().getBelief("plan_a_executed").setFact(Integer.valueOf(0));
		getBeliefbase().getBelief("plan_b_executed").setFact(Integer.valueOf(0));
		getBeliefbase().getBelief("plan_c_executed").setFact(Integer.valueOf(0));
		return ret;
	}
}
