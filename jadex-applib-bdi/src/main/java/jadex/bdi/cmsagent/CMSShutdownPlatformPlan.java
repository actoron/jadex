package jadex.bdi.cmsagent;

import jadex.bdi.runtime.Plan;
import jadex.bridge.fipa.CMSShutdownPlatform;
import jadex.bridge.fipa.Done;

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
