package jadex.bdi.cmsagent;

import jadex.base.fipa.CMSResumeComponent;
import jadex.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

/**
 *  Resume an component.
 */
public class CMSResumeComponentPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		CMSResumeComponent ra = (CMSResumeComponent)getParameter("action").getValue();

		IGoal rag = createGoal("cms_suspend_component");
		rag.getParameter("componentidentifier").setValue(ra.getComponentIdentifier());
		dispatchSubgoalAndWait(rag);

		getParameter("result").setValue(new Done(ra));
	}
}
