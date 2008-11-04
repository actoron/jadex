package jadex.bdi.planlib.ams;

import jadex.adapter.base.fipa.AMSStartAgent;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IAgentIdentifier;

/**
 *  Start an agent on a remote ams.
 */
public class AMSRemoteStartAgentPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		AMSStartAgent sa = new AMSStartAgent();
		sa.setAgentIdentifier((IAgentIdentifier)getParameter("agentidentifier").getValue());

		IGoal req = createGoal("rp_initiate");
		req.getParameter("receiver").setValue(getParameter("ams").getValue());
		req.getParameter("action").setValue(sa);
		dispatchSubgoalAndWait(req);
	}
}
