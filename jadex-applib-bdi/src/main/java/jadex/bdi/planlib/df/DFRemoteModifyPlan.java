package jadex.bdi.planlib.df;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.DFComponentDescription;
import jadex.bridge.fipa.DFModify;
import jadex.bridge.fipa.Done;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.df.IDFComponentDescription;

import java.util.Date;

/**
 *  Modify df entry on a remote platform.
 */
public class DFRemoteModifyPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instantiated plan instance from the scheduler.
	 */
	public void body()
	{
		DFModify mo = new DFModify();
		
		IDFComponentDescription desc = (IDFComponentDescription)getParameter("description").getValue();
		Number lt = (Number)getParameter("leasetime").getValue();
		// When AID is ommited, enter self. Hack???
		if(desc.getName()==null || lt!=null)
		{
			IComponentIdentifier	bid	= desc.getName()!=null ? desc.getName() : getScope().getComponentIdentifier();
			Date	leasetime	= lt==null ? desc.getLeaseTime() : new Date(getTime()+lt.longValue());
			desc	= new DFComponentDescription(bid, desc.getServices(), desc.getProtocols(), desc.getOntologies(), desc.getLanguages(), leasetime);
		}
		mo.setComponentDescription(desc);

		IGoal req = createGoal("rp_initiate");
		req.getParameter("receiver").setValue(getParameter("df").getValue());
		req.getParameter("action").setValue(mo);
		req.getParameter("ontology").setValue(SFipa.COMPONENT_MANAGEMENT_ONTOLOGY_NAME);
		dispatchSubgoalAndWait(req);

		getParameter("result").setValue(((DFModify)((Done)req.getParameter("result").getValue()).getAction()).getResult());
	}
}
