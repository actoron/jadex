package jadex.bdi.planlib.cms;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.cms.IComponentManagementService;

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
		IComponentManagementService	cms	= (IComponentManagementService)getServiceContainer().getRequiredService("cms").get(this);
		IComponentIdentifier	root	= getScope().getComponentIdentifier();
		boolean	foundroot	= false;
		while(!foundroot)
		{
			IComponentIdentifier	parent	= (IComponentIdentifier)cms.getParent(root).get(this);
			if(parent==null)
				foundroot	= true;
			else
				root	= parent;
		}
		
		cms.resumeComponent(root).get(this);
		cms.destroyComponent(root);
	}
}
