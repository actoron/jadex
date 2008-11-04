package jadex.bdi.planlib.ams;

import jadex.adapter.base.fipa.AMSResumeAgent;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IAgentIdentifier;

/**
 *  Suspend an agent on a remote ams.
 */
public class AMSRemoteResumeAgentPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		AMSResumeAgent ra = new AMSResumeAgent();
		ra.setAgentIdentifier((IAgentIdentifier)getParameter("agentidentifier").getValue());

		IGoal req = createGoal("rp_initiate");
		req.getParameter("receiver").setValue(getParameter("ams").getValue());
		req.getParameter("action").setValue(ra);
		dispatchSubgoalAndWait(req);
	}
}
