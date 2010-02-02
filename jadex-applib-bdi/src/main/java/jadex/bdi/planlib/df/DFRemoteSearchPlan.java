package jadex.bdi.planlib.df;

import jadex.adapter.base.fipa.DFSearch;
import jadex.adapter.base.fipa.Done;
import jadex.adapter.base.fipa.IDFComponentDescription;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.ISearchConstraints;

/**
 *  Search at a remote DF.
 */
public class DFRemoteSearchPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		DFSearch se = new DFSearch();
		se.setComponentDescription((IDFComponentDescription)getParameter("description").getValue());
		se.setSearchConstraints((ISearchConstraints)getParameter("constraints").getValue());

		IGoal req = createGoal("rp_initiate");
		req.getParameter("receiver").setValue(getParameter("df").getValue());
		req.getParameter("action").setValue(se);
		req.getParameter("ontology").setValue(SFipa.AGENT_MANAGEMENT_ONTOLOGY_NAME);
		dispatchSubgoalAndWait(req);

		getParameterSet("result").addValues(((DFSearch)((Done)req.getParameter("result").getValue()).getAction()).getResults());
	}
}
