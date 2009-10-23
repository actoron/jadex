package jadex.bdi.planlib.ams;

import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;

/**
 *  Plan for suspending a Jadex agent on the platform.
 */
public class AMSLocalSuspendAgentPlan extends Plan
{
	/**
	 *  Execute a plan.
	 */
	public void body()
	{
		IComponentIdentifier	aid	= (IComponentIdentifier)getParameter("agentidentifier").getValue();
	
		SyncResultListener lis = new SyncResultListener();
		((IAMS)getScope().getPlatform().getService(IAMS.class, SFipa.AMS_SERVICE)).suspendAgent(aid, lis);
		IComponentDescription desc = (IComponentDescription)lis.waitForResult();
		
		getParameter("agentdescription").setValue(desc);
	}
}
