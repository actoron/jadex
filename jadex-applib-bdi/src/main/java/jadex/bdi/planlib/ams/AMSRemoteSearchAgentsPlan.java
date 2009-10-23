package jadex.bdi.planlib.ams;

import jadex.adapter.base.fipa.AMSSearchAgents;
import jadex.adapter.base.fipa.Done;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentDescription;
import jadex.bridge.ISearchConstraints;

/**
 *  Search for agents on a remote platform.
 */
public class AMSRemoteSearchAgentsPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		AMSSearchAgents sa = new AMSSearchAgents();
		sa.setAgentDescription((IComponentDescription)getParameter("description").getValue());
		sa.setSearchConstraints((ISearchConstraints)getParameter("constraints").getValue());

		IGoal req = createGoal("rp_initiate");
		req.getParameter("receiver").setValue(getParameter("ams").getValue());
		req.getParameter("action").setValue(sa);
		req.getParameter("ontology").setValue(SFipa.AGENT_MANAGEMENT_ONTOLOGY_NAME);
		dispatchSubgoalAndWait(req);

		getParameterSet("result").addValues(((AMSSearchAgents)((Done)req.getParameter("result")
			.getValue()).getAction()).getAgentDescriptions());
	}
}

