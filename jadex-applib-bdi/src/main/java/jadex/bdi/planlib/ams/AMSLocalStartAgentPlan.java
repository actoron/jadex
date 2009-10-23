package jadex.bdi.planlib.ams;

import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.Plan;
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
			((IAMS)getScope().getPlatform().getService(IAMS.class, SFipa.AMS_SERVICE)).startAgent(agentidentifier, lis);
			lis.waitForResult();
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			fail(e); // Do not show exception on console. 
		}
	}
}
