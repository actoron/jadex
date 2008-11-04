package jadex.bdi.testcases.planlib;

import jadex.bdi.runtime.Plan;

/**
 *  Make a simple proposal based on a random value.
 */
public class ICNPMakeProposalPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		getLogger().info("proposal plan called");
		getParameter("proposal").setValue(getBeliefbase().getBelief("offer").getFact());
	}
}
