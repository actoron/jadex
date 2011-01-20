package jadex.bdi.planlib.cms;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.IFuture;

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
		IComponentIdentifier	cid	= (IComponentIdentifier)getParameter("componentidentifier").getValue();
	
		IFuture ret =((IComponentManagementService)getScope().getRequiredService("cms").get(this)).suspendComponent(cid);
		IComponentDescription desc = (IComponentDescription)ret.get(this);
		
		getParameter("componentdescription").setValue(desc);
	}
}
