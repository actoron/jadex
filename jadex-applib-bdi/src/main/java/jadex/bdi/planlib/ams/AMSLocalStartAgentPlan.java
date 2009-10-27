package jadex.bdi.planlib.ams;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentIdentifier;

/**
 *  Plan for starting a Jadex agent on the platform.
 */
public class AMSLocalStartAgentPlan extends Plan
{
	/**
	 *  Execute a plan.
	 */
	public void body()
	{
		IComponentIdentifier	agentidentifier	= (IComponentIdentifier)getParameter("agentidentifier").getValue();

		try
		{
			SyncResultListener lis = new SyncResultListener();
			((IComponentExecutionService)getScope().getServiceContainer().getService(IComponentExecutionService.class)).startComponent(agentidentifier, lis);
			lis.waitForResult();
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			fail(e); // Do not show exception on console. 
		}
	}
}
