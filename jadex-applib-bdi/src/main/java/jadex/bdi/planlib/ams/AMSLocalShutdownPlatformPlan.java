package jadex.bdi.planlib.ams;

import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentExecutionService;

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
		((IComponentExecutionService)getScope().getServiceContainer().getService(IComponentExecutionService.class)).shutdownPlatform(lis);
		lis.waitForResult();
	}
}
