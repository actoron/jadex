package jadex.bdi.planlib.df;

import jadex.base.fipa.DFDeregister;
import jadex.base.fipa.IDF;
import jadex.base.fipa.IDFComponentDescription;
import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.service.SServiceProvider;

/**
 *  Register on a remote platform.
 */
public class DFRemoteDeregisterPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		IDFComponentDescription desc = (IDFComponentDescription)getParameter("description").getValue();
		if(desc==null || desc.getName()==null)
		{
			IDF df = (IDF)SServiceProvider.getService(getScope().getServiceProvider(), IDF.class).get(this);
			desc = df.createDFComponentDescription(getScope().getComponentIdentifier(), null);
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
