package jadex.bdi.planlib.ams;

import jadex.bdi.runtime.Plan;

/**
 *  Shutdown the platform.
 */
// Todo: remove from AMS?
public class AMSLocalShutdownPlatformPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		SyncResultListener lis = new SyncResultListener();
		getScope().getServiceContainer().shutdown(lis);
		lis.waitForResult();
	}
}
