package jadex.bdi.planlib.protocols.cancelmeta;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Default plan to decide about cancel requests.
 *  Always returns true.
 */
public class CMApproveCancelPlan	extends Plan
{
	/**
	 *  The plan body.
	 */
	public void	body()
	{
		getParameter("result").setValue(Boolean.TRUE);
	}
}
