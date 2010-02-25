package jadex.bdi.cmsagent;

import jadex.adapter.base.fipa.CMSShutdownPlatform;
import jadex.adapter.base.fipa.Done;
import jadex.bdi.runtime.Plan;

/**
 *  Isuue a platform shutdown.
 */
public class CMSShutdownPlatformPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		CMSShutdownPlatform sd = (CMSShutdownPlatform)getParameter("action").getValue();
		dispatchSubgoalAndWait(createGoal("cms_shutdown_platform"));
		getParameter("result").setValue(new Done(sd));
	}
}
