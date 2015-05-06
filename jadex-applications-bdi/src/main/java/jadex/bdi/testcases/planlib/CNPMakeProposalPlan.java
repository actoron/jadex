package jadex.bdi.testcases.planlib;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Make a simple proposal based on a random value.
 */
public class CNPMakeProposalPlan extends Plan
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
