package jadex.bdi.testcases.planlib;

import jadex.bdi.runtime.Plan;

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
		getBeliefbase().getBelief("acceptplan_invoked").setFact(new Integer(inv.intValue()+1));
		getParameter("accept").setValue(new Boolean(cfp.doubleValue()>100));
	}
}
