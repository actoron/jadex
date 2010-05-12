package deco4mas.examples.agentNegotiation.sa;

import jadex.bdi.runtime.Plan;

/**
 * Signed with Sma
 */
public class ProposalAcceptedPlan extends Plan
{
	/**
	 * The plan body.
	 */
	public void body()
	{
		System.out.println(((String) getBeliefbase().getBelief("providedService").getFact()).substring(0, 1) + ": "
			+ this.getComponentName() + " signed with Sma [" + getParameter("proposal").getValue() + "]");
		getParameter("result").setValue(this.getComponentIdentifier());
	}
}
