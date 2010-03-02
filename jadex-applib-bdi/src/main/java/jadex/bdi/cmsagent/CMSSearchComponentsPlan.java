package jadex.bdi.cmsagent;

import jadex.base.fipa.CMSSearchComponents;
import jadex.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentDescription;

/**
 *  Search for components.
 */
public class CMSSearchComponentsPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		CMSSearchComponents sa = (CMSSearchComponents)getParameter("action").getValue();

		IGoal sag = createGoal("cms_search_components");
		sag.getParameter("description").setValue(sa.getComponentDescription());
		sag.getParameter("constraints").setValue(sa.getSearchConstraints());
		dispatchSubgoalAndWait(sag);

		sa.setComponentDescriptions((IComponentDescription[])sag.getParameterSet("result").getValues());
		getParameter("result").setValue(new Done(sa));
	}
}
