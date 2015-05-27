package jadex.bdi.cmsagent;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.CMSResumeComponent;
import jadex.bridge.fipa.Done;

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
