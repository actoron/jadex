package jadex.bdi.planlib.ams;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentIdentifier;

/**
 *  Plan for resuming a Jadex agent on the platform.
 */
public class AMSLocalResumeAgentPlan extends Plan
{
	/**
	 *  Execute a plan.
	 */
	public void body()
	{	
		IComponentIdentifier	aid	= (IComponentIdentifier)getParameter("agentidentifier").getValue();
		
		SyncResultListener lis = new SyncResultListener();
		((IComponentExecutionService)getScope().getServiceContainer().getService(IComponentExecutionService.class)).resumeComponent(aid, lis);
		IComponentDescription desc =  (IComponentDescription)lis.waitForResult();
		
		getParameter("agentdescription").setValue(desc);
	}
	
}
