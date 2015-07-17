package jadex.bdi.planlib.cms;

import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;


/**
 *  Plan for terminating a Jadex component on the platform.
 */
public class CMSLocalDestroyComponentPlan extends Plan
{
	/**
	 *  Execute a plan.
	 */
	public void body()
	{	
		IComponentIdentifier cid = (IComponentIdentifier)getParameter("componentidentifier").getValue();

		try
		{
			IFuture ret = ((IComponentManagementService)getAgent().getComponentFeature(IRequiredServicesFeature.class).getRequiredService("cms").get()).destroyComponent(cid);
			ret.get();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail(e); // Do not show exception on console. 
		}
	}
}
