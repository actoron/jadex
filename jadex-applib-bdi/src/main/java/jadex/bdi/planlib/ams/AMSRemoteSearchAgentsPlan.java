package jadex.bdi.planlib.ams;

import jadex.adapter.base.fipa.AMSSearchAgents;
import jadex.adapter.base.fipa.Done;
import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.adapter.base.fipa.ISearchConstraints;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

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
		sa.setAgentDescription((IAMSAgentDescription)getParameter("description").getValue());
		sa.setSearchConstraints((ISearchConstraints)getParameter("constraints").getValue());

		IGoal req = createGoal("rp_initiate");
		req.getParameter("receiver").setValue(getParameter("ams").getValue());
		req.getParameter("action").setValue(sa);
		dispatchSubgoalAndWait(req);

		getParameterSet("result").addValues(((AMSSearchAgents)((Done)req.getParameter("result")
			.getValue()).getAction()).getAgentDescriptions());
	}
}

