package jadex.bdi.testcases.planlib;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Decide about the next round and the cfp for it.
 */
public class EADecideIterationGenPlan extends Plan
{	
	/**
	 *  The plan body.
	 */
	public void body()
	{
		IOfferGenerator gen = (IOfferGenerator)getParameter("cfp_info").getValue();
		gen.setNextRound();
		
		// Use offer only when limit was not reached yet.
		if(!gen.getLimitOffer().equals(gen.getLastOffer()))
			getParameter("cfp").setValue(gen.getCurrentOffer());
	}
	
	public void passed()
	{
		getLogger().info("passed: "+this);
	}

	public void failed()
	{
		getLogger().info("failed: "+this);
	}

	public void aborted()
	{
		getLogger().info("aborted: "+this);
	}
}
