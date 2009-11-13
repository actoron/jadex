package jadex.bdi.planlib.df;

import jadex.adapter.base.fipa.DFDeregister;
import jadex.adapter.base.fipa.IDF;
import jadex.adapter.base.fipa.IDFAgentDescription;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

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
		IDFAgentDescription desc = (IDFAgentDescription)getParameter("description").getValue();
		if(desc==null || desc.getName()==null)
		{
			IDF df = (IDF)getScope().getServiceContainer().getService(IDF.class);
			desc = df.createDFAgentDescription(getScope().getComponentIdentifier(), null);
//			IComponentIdentifier	bid	= getScope().getAgentIdentifier();
//			desc.setName(bid);
		}

		DFDeregister dre = new DFDeregister();
		dre.setAgentDescription(desc);
		IGoal req = createGoal("rp_initiate");
		req.getParameter("receiver").setValue(getParameter("df").getValue());
		req.getParameter("action").setValue(dre);
		req.getParameter("ontology").setValue(SFipa.AGENT_MANAGEMENT_ONTOLOGY_NAME);
		dispatchSubgoalAndWait(req);
	}
}
