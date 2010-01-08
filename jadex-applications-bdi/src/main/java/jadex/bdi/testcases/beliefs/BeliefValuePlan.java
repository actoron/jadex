package jadex.bdi.testcases.beliefs;

import jadex.adapter.base.test.TestReport;
import jadex.bdi.runtime.Plan;
import jadex.commons.SUtil;

/**
 *  Test initial belief values.
 */
public class BeliefValuePlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new countdown plan.
	 */
	public BeliefValuePlan()
	{
		getLogger().info("Created: "+this);
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		TestReport tr = new TestReport("#1", "Test initial belief values.");

		String bel_a = (String)getBeliefbase().getBelief("cap_a.bel").getFact();
		String bel_b = (String)getBeliefbase().getBelief("cap_b.bel").getFact();
		String bel_c = (String)getBeliefbase().getBelief("cap_c.bel").getFact();

		String[] belset_a = (String[])getBeliefbase().getBeliefSet("cap_a.belset").getFacts();
		String[] belset_b = (String[])getBeliefbase().getBeliefSet("cap_b.belset").getFacts();
		String[] belset_c = (String[])getBeliefbase().getBeliefSet("cap_c.belset").getFacts();

		tr.setSucceeded(true);
		if(!bel_a.equals("agent_initial_bel"))
		{
			getLogger().info("bel_a error: "+bel_a);
			tr.setSucceeded(false);
		}
		if(!bel_b.equals("capability_initial_bel"))
		{
			getLogger().info("bel_b error: "+bel_b);
			tr.setSucceeded(false);
		}
		if(!bel_c.equals("capability_default_bel"))
		{
			getLogger().info("bel_c error: "+bel_c);
			tr.setSucceeded(false);
		}

		if(!belset_a[0].equals("agent_initial_belset_0") || !belset_a[1].equals("agent_initial_belset_1"))
		{
			getLogger().info("belset_a error: "+ SUtil.arrayToString(belset_a));
			tr.setSucceeded(false);
		}
		if(!belset_b[0].equals("capability_initial_belset_0") || !belset_b[1].equals("capability_initial_belset_1"))
		{
			getLogger().info("belset_b error: "+ SUtil.arrayToString(belset_b));
			tr.setSucceeded(false);
		}
		if(!belset_c[0].equals("capability_default_belset_0") || !belset_c[1].equals("capability_default_belset_1"))
		{
			getLogger().info("belset_c error: "+ SUtil.arrayToString(belset_c));
			tr.setSucceeded(false);
		}

		if(!tr.isSucceeded())
		{
			tr.setReason("Some initial value was not set correctly.");
		}

		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}

