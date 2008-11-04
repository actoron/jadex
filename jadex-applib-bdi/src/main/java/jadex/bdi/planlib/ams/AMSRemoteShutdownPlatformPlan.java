package jadex.bdi.planlib.ams;

import jadex.adapter.base.fipa.AMSShutdownPlatform;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

/**
 *  Shutdown a remote platform.
 */
public class AMSRemoteShutdownPlatformPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		AMSShutdownPlatform sp = new AMSShutdownPlatform();

		IGoal req = createGoal("rp_initiate");
		req.getParameter("receiver").setValue(getParameter("ams").getValue());
		req.getParameter("action").setValue(sp);
		dispatchSubgoalAndWait(req);
	}
}

