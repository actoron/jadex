package jadex.bdi.planlib.cms;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentIdentifier;
import jadex.service.IServiceContainer;

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

		final IServiceContainer plat = getScope().getServiceContainer();
		try
		{
			SyncResultListener lis = new SyncResultListener();
			((IComponentManagementService)plat.getService(IComponentManagementService.class)).getExternalAccess(aid, lis);
			Object ret = lis.waitForResult();
			getParameter("result").setValue(ret);
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			fail(e); // Do not show exception on console. 
		}
	}
}
