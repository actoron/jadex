package jadex.bdi.testcases.planlib;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Decide if the next iteration should be done.
 */
public class ICNPNextroundInfoPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		// Allow at most 3 negotiation rounds and do not alter cfp, cfp_info or participants.
		if(getParameterSet("history").size()<3)
		{
			getParameter("iterate").setValue(Boolean.TRUE);
		}
		else
		{
			getParameter("iterate").setValue(Boolean.FALSE);
		}
	}
}
