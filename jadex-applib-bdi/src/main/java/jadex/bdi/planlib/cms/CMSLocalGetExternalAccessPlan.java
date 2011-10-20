package jadex.bdi.planlib.cms;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;

/**
 *  Plan for terminating a Jadex component on the platform.
 */
public class CMSLocalGetExternalAccessPlan extends Plan
{
	/**
	 *  Execute a plan.
	 */
	public void body()
	{	
		IComponentIdentifier aid = (IComponentIdentifier)getParameter("componentidentifier").getValue();

		try
		{
			IFuture fut = ((IComponentManagementService)getServiceContainer().getRequiredService("cms").get(this)).getExternalAccess(aid);
			Object ret = fut.get(this);
			getParameter("result").setValue(ret);
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			fail(e); // Do not show exception on console. 
		}
	}
}
