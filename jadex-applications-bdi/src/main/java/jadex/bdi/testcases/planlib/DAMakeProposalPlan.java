package jadex.bdi.testcases.planlib;

import jadex.bdiv3x.runtime.Plan;


/**
 * Decides if the actual price of the auction is acceptable.
 */
public class DAMakeProposalPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		Double maxprice = (Double)getBeliefbase().getBelief("max_price").getFact();
		Double price = (Double)getParameter("cfp").getValue();
		Boolean accept = Boolean.valueOf(price.doubleValue() <= maxprice.doubleValue());
		//System.out.println(getAgentName()+" accept="+accept+" price="+price+" max="+maxprice);
		getParameter("accept").setValue(accept);
	}
}
