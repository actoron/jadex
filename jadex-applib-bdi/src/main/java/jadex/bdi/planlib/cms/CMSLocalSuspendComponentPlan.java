package jadex.bdi.planlib.cms;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentIdentifier;

/**
 *  Plan for suspending a Jadex component on the platform.
 */
public class CMSLocalSuspendComponentPlan extends Plan
{
	/**
	 *  Execute a plan.
	 */
	public void body()
	{
		IComponentIdentifier	aid	= (IComponentIdentifier)getParameter("componentidentifier").getValue();
	
		SyncResultListener lis = new SyncResultListener();
		((IComponentManagementService)getScope().getServiceContainer().getService(IComponentManagementService.class)).suspendComponent(aid, lis);
		IComponentDescription desc = (IComponentDescription)lis.waitForResult();
		
		getParameter("componentdescription").setValue(desc);
	}
}
