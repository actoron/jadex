package jadex.bdi.amsagent;

import jadex.adapter.base.fipa.AMSSearchAgents;
import jadex.adapter.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentDescription;

/**
 *  Search for agents.
 */
public class AMSSearchAgentsPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		AMSSearchAgents sa = (AMSSearchAgents)getParameter("action").getValue();

		IGoal sag = createGoal("ams_search_agents");
		sag.getParameter("description").setValue(sa.getAgentDescription());
		sag.getParameter("constraints").setValue(sa.getSearchConstraints());
		dispatchSubgoalAndWait(sag);

		sa.setAgentDescriptions((IComponentDescription[])sag.getParameterSet("result").getValues());
		getParameter("result").setValue(new Done(sa));
	}
}
