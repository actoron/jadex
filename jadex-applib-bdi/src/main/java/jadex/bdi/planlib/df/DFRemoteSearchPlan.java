package jadex.bdi.planlib.df;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.fipa.DFSearch;
import jadex.bridge.fipa.Done;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.df.IDFComponentDescription;

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
		se.setRemote(getParameter("remote").getValue()!=null? 
			((Boolean)getParameter("remote").getValue()).booleanValue(): false);
		
		IGoal req = createGoal("rp_initiate");
		req.getParameter("receiver").setValue(getParameter("df").getValue());
		req.getParameter("action").setValue(se);
		req.getParameter("ontology").setValue(SFipa.COMPONENT_MANAGEMENT_ONTOLOGY_NAME);
		dispatchSubgoalAndWait(req);

		getParameterSet("result").addValues(((DFSearch)((Done)req.getParameter("result").getValue()).getAction()).getResults());
	}
}
