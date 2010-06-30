package jadex.bdi.planlib.cms;

import jadex.bdi.runtime.Plan;

/**
 *  Shutdown the platform.
 */
// Todo: remove from CMS?
public class CMSLocalShutdownPlatformPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		getScope().getServiceContainer().shutdown().get(this);
	}
}
