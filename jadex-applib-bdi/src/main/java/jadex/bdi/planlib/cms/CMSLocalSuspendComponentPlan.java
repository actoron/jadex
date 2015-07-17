package jadex.bdi.planlib.cms;

import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;

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
	
		IFuture ret =((IComponentManagementService)getAgent().getComponentFeature(IRequiredServicesFeature.class).getRequiredService("cms").get()).suspendComponent(cid);
		IComponentDescription desc = (IComponentDescription)ret.get();
		
		getParameter("componentdescription").setValue(desc);
	}
}
