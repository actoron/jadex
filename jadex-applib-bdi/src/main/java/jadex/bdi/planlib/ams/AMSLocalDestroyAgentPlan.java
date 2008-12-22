package jadex.bdi.planlib.ams;

import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IPlatform;


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
		IAgentIdentifier	aid	= (IAgentIdentifier)getParameter("agentidentifier").getValue();

		final IPlatform plat	= getScope().getPlatform();
		try
		{
			SyncResultListener lis = new SyncResultListener();
			((IAMS)plat.getService(IAMS.class, SFipa.AMS_SERVICE)).destroyAgent(aid, lis);
			lis.waitForResult();
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			fail(e); // Do not show exception on console. 
		}
	}
}
