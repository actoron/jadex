package jadex.bdi.testcases.planlib;

import jadex.bdi.planlib.protocols.AuctionDescription;
import jadex.bdiv3x.runtime.Plan;


/**
 *  Decide on participation.
 */
public class EADecideParticipationPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		boolean participate = ((Boolean)getBeliefbase().getBelief("participate").getFact()).booleanValue();
		getLogger().info(getComponentName()+" deciding on participation in auction "
			+((AuctionDescription)getParameter("auction_description").getValue()).getTopic()+" "+participate);
		getParameter("participate").setValue(Boolean.valueOf(participate));
	}
}
