package jadex.bdi.planlib.cms;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.component.IRequiredServicesFeature;
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
		IComponentManagementService	cms	= (IComponentManagementService)getInterpreter().getComponentFeature(IRequiredServicesFeature.class).getRequiredService("cms").get();
		IComponentIdentifier	root	= getScope().getComponentIdentifier();
		boolean	foundroot	= false;
		while(!foundroot)
		{
			IComponentIdentifier	parent	= (IComponentIdentifier)cms.getParent(root).get();
			if(parent==null)
				foundroot	= true;
			else
				root	= parent;
		}
		
		cms.resumeComponent(root).get();
		cms.destroyComponent(root);
	}
}
