package jadex.bdi.planlib.cms;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.IFuture;
import jadex.service.IServiceContainer;


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

		final IServiceContainer plat	= getScope().getServiceContainer();
		try
		{
			IFuture ret = ((IComponentManagementService)plat.getService(IComponentManagementService.class).get(this)).destroyComponent(cid);
			ret.get(this);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail(e); // Do not show exception on console. 
		}
	}
}
