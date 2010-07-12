package jadex.bdi.planlib.cms;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.IFuture;
import jadex.service.SServiceProvider;

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
	
		IFuture ret =((IComponentManagementService)SServiceProvider.getService(
			getScope().getServiceProvider(), IComponentManagementService.class).get(this)).suspendComponent(cid);
		IComponentDescription desc = (IComponentDescription)ret.get(this);
		
		getParameter("componentdescription").setValue(desc);
	}
}
