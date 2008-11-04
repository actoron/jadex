package jadex.bdi.dfagent;

import jadex.adapter.base.fipa.DFSearch;
import jadex.adapter.base.fipa.Done;
import jadex.adapter.base.fipa.IDFAgentDescription;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

/**
 *  The df search plan has the task to receive a message 
 *  andc reate a corresponding goal.
 */
public class DFSearchPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		DFSearch sa = (DFSearch)getParameter("action").getValue();

		IGoal sag = createGoal("df_search");
		sag.getParameter("description").setValue(sa.getAgentDescription());
		sag.getParameter("constraints").setValue(sa.getSearchConstraints());
		dispatchSubgoalAndWait(sag);

		sa.setResults((IDFAgentDescription[])sag.getParameterSet("result").getValues());
		getParameter("result").setValue(new Done(sa));
	}
}
