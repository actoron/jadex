package jadex.bdi.planlib.cms;

import jadex.bdi.runtime.Plan;
import jadex.service.IServiceContainer;

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
		// todo: hack fix me
		((IServiceContainer)getScope().getServiceProvider()).shutdown().get(this);
	}
}
