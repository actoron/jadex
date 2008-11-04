package jadex.bdi.planlib.protocols.cancelmeta;

import jadex.bdi.runtime.Plan;

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
