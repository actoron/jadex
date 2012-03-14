package jadex.bdi.planlib.cms;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.CMSResumeComponent;
import jadex.bridge.fipa.SFipa;

/**
 *  Suspend an component on a remote cms.
 */
public class CMSRemoteResumeComponentPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		CMSResumeComponent ra = new CMSResumeComponent();
		ra.setComponentIdentifier((IComponentIdentifier)getParameter("componentidentifier").getValue());

		IGoal req = createGoal("rp_initiate");
		req.getParameter("receiver").setValue(getParameter("cms").getValue());
		req.getParameter("action").setValue(ra);
		req.getParameter("ontology").setValue(SFipa.COMPONENT_MANAGEMENT_ONTOLOGY_NAME);
		dispatchSubgoalAndWait(req);
	}
}
