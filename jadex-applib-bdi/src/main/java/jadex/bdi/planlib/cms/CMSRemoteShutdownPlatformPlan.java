package jadex.bdi.planlib.cms;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.fipa.CMSShutdownPlatform;
import jadex.bridge.fipa.SFipa;

/**
 *  Shutdown a remote platform.
 */
public class CMSRemoteShutdownPlatformPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		CMSShutdownPlatform sp = new CMSShutdownPlatform();

		IGoal req = createGoal("rp_initiate");
		req.getParameter("receiver").setValue(getParameter("cms").getValue());
		req.getParameter("action").setValue(sp);
		req.getParameter("ontology").setValue(SFipa.COMPONENT_MANAGEMENT_ONTOLOGY_NAME);
		dispatchSubgoalAndWait(req);
	}
}

