package jadex.bdi.planlib.ams;

import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.Plan;

/**
 *  Shutdown the platform.
 */
public class AMSLocalShutdownPlatformPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		SyncResultListener lis = new SyncResultListener();
		((IAMS)getScope().getPlatform().getService(IAMS.class, SFipa.AMS_SERVICE)).shutdownPlatform(lis);
		lis.waitForResult();
	}
}
