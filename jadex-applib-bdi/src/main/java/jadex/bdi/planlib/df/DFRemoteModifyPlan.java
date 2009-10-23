package jadex.bdi.planlib.df;

import jadex.adapter.base.fipa.DFModify;
import jadex.adapter.base.fipa.Done;
import jadex.adapter.base.fipa.IDF;
import jadex.adapter.base.fipa.IDFAgentDescription;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;

import java.util.Date;

/**
 *  Modify df entry on a remote platform.
 */
public class DFRemoteModifyPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		DFModify mo = new DFModify();
		
		IDFAgentDescription desc = (IDFAgentDescription)getParameter("description").getValue();
		Number lt = (Number)getParameter("leasetime").getValue();
		// When AID is ommited, enter self. Hack???
		if(desc.getName()==null || lt!=null)
		{
			IDF	dfservice	= (IDF)getScope().getServiceContainer().getService(IDF.class, SFipa.DF_SERVICE);
			IComponentIdentifier	bid	= desc.getName()!=null ? desc.getName() : getScope().getAgentIdentifier();
			Date	leasetime	= lt==null ? desc.getLeaseTime() : new Date(getTime()+lt.longValue());
			desc	= dfservice.createDFAgentDescription(bid, desc.getServices(), desc.getLanguages(), desc.getOntologies(), desc.getProtocols(), leasetime);
		}
		mo.setAgentDescription(desc);

		IGoal req = createGoal("rp_initiate");
		req.getParameter("receiver").setValue(getParameter("df").getValue());
		req.getParameter("action").setValue(mo);
		req.getParameter("ontology").setValue(SFipa.AGENT_MANAGEMENT_ONTOLOGY_NAME);
		dispatchSubgoalAndWait(req);

		getParameter("result").setValue(((DFModify)((Done)req.getParameter("result").getValue()).getAction()).getResult());
	}
}
