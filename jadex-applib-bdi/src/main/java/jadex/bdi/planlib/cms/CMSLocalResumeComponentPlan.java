package jadex.bdi.planlib.cms;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;

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
		
		IFuture ret = ((IComponentManagementService)getServiceContainer().getRequiredService("cms").get(this)).resumeComponent(aid);
		IComponentDescription desc =  (IComponentDescription) ret.get(this);
		
		getParameter("componentdescription").setValue(desc);
	}
	
}
