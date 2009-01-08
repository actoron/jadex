package jadex.bdi.planlib.ams;

import jadex.adapter.base.fipa.AMSCreateAgent;
import jadex.adapter.base.fipa.Done;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

import java.util.Map;

/**
 *  Create an agent on a remote ams.
 */
public class AMSRemoteCreateAgentPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		AMSCreateAgent ca = new AMSCreateAgent();
		ca.setType((String)getParameter("type").getValue());
		ca.setName((String)getParameter("name").getValue());
		ca.setConfiguration((String)getParameter("configuration").getValue());
		ca.setArguments((Map)getParameter("arguments").getValue());
		ca.setStart(((Boolean)getParameter("start").getValue()).booleanValue());

		IGoal req = createGoal("rp_initiate");
		req.getParameter("receiver").setValue(getParameter("ams").getValue());
		req.getParameter("action").setValue(ca);
		req.getParameter("ontology").setValue(SFipa.AGENT_MANAGEMENT_ONTOLOGY_NAME);
		dispatchSubgoalAndWait(req);

		getParameter("agentidentifier").setValue(((AMSCreateAgent)((Done)req.getParameter("result").getValue()).getAction()).getAgentIdentifier());
	}
}
