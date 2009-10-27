package jadex.bdi.planlib.ams;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentIdentifier;
import jadex.service.IServiceContainer;


/**
 *  Plan for terminating a Jadex agent on the platform.
 */
public class AMSLocalDestroyAgentPlan extends Plan
{
	/**
	 *  Execute a plan.
	 */
	public void body()
	{	
		IComponentIdentifier	aid	= (IComponentIdentifier)getParameter("agentidentifier").getValue();

		final IServiceContainer plat	= getScope().getServiceContainer();
		try
		{
			SyncResultListener lis = new SyncResultListener();
			((IComponentExecutionService)plat.getService(IComponentExecutionService.class)).destroyComponent(aid, lis);
			lis.waitForResult();
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			fail(e); // Do not show exception on console. 
		}
	}
}
