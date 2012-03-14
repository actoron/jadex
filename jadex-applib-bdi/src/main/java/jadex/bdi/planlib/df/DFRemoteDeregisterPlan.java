package jadex.bdi.planlib.df;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.fipa.DFComponentDescription;
import jadex.bridge.fipa.DFDeregister;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.df.IDFComponentDescription;

/**
 *  Register on a remote platform.
 */
public class DFRemoteDeregisterPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instantiated plan instance from the scheduler.
	 */
	public void body()
	{
		IDFComponentDescription desc = (IDFComponentDescription)getParameter("description").getValue();
		if(desc==null || desc.getName()==null)
		{
			desc = new DFComponentDescription(getScope().getComponentIdentifier());
		}

		DFDeregister dre = new DFDeregister();
		dre.setComponentDescription(desc);
		IGoal req = createGoal("rp_initiate");
		req.getParameter("receiver").setValue(getParameter("df").getValue());
		req.getParameter("action").setValue(dre);
		req.getParameter("ontology").setValue(SFipa.COMPONENT_MANAGEMENT_ONTOLOGY_NAME);
		dispatchSubgoalAndWait(req);
	}
}
