package jadex.bdi.testcases.planlib;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Decide about the acceptance of the final offer.
 */
public class EADecideAcceptancePlan extends Plan
{	
	/**
	 *  The plan body.
	 */
	public void body()
	{
		Double cfp = (Double)getParameter("cfp").getValue();
		Integer inv = (Integer)getBeliefbase().getBelief("acceptplan_invoked").getFact();
		getBeliefbase().getBelief("acceptplan_invoked").setFact(Integer.valueOf(inv.intValue()+1));
		getParameter("accept").setValue(Boolean.valueOf(cfp.doubleValue()>100));
	}
}
