package jadex.bdi.planlib.cms;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.CMSDestroyComponent;
import jadex.bridge.fipa.SFipa;

/**
 *  Destroy an component on a remote cms.
 */
public class CMSRemoteDestroyComponentPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		CMSDestroyComponent da = new CMSDestroyComponent();
		da.setComponentIdentifier((IComponentIdentifier)getParameter("componentidentifier").getValue());

		IGoal req = createGoal("rp_initiate");
		req.getParameter("receiver").setValue(getParameter("cms").getValue());
		req.getParameter("action").setValue(da);
		req.getParameter("ontology").setValue(SFipa.COMPONENT_MANAGEMENT_ONTOLOGY_NAME);
		dispatchSubgoalAndWait(req);
	}
}
