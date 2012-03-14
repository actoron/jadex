package jadex.bdi.cmsagent;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.fipa.CMSSearchComponents;
import jadex.bridge.fipa.Done;
import jadex.bridge.service.types.cms.IComponentDescription;

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
		sag.getParameter("remote").setValue(sa.isRemote()? Boolean.TRUE: Boolean.FALSE);
		dispatchSubgoalAndWait(sag);

		sa.setComponentDescriptions((IComponentDescription[])sag.getParameterSet("result").getValues());
		getParameter("result").setValue(new Done(sa));
	}
}
