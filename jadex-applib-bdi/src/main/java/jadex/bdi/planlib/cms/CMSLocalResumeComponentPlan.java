package jadex.bdi.planlib.cms;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentIdentifier;

/**
 *  Plan for resuming a Jadex component on the platform.
 */
public class CMSLocalResumeComponentPlan extends Plan
{
	/**
	 *  Execute a plan.
	 */
	public void body()
	{	
		IComponentIdentifier	aid	= (IComponentIdentifier)getParameter("componentidentifier").getValue();
		
		SyncResultListener lis = new SyncResultListener();
		((IComponentManagementService)getScope().getServiceContainer().getService(IComponentManagementService.class)).resumeComponent(aid, lis);
		IComponentDescription desc =  (IComponentDescription)lis.waitForResult();
		
		getParameter("componentdescription").setValue(desc);
	}
	
}
