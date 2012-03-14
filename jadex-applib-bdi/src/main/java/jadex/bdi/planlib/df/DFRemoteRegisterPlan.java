package jadex.bdi.planlib.df;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.DFComponentDescription;
import jadex.bridge.fipa.DFRegister;
import jadex.bridge.fipa.Done;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.df.IDFComponentDescription;

import java.util.Date;

/**
 *  Register on a remote platform.
 */
public class DFRemoteRegisterPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instantiated plan instance from the scheduler.
	 */
	public void body()
	{
//		System.out.println("df register");
		DFRegister re = new DFRegister();
		IDFComponentDescription desc = (IDFComponentDescription)getParameter("description").getValue();
		Number lt = (Number)getParameter("leasetime").getValue();
		// When AID is omitted, enter self. Hack???
		if(desc.getName()==null || lt!=null)
		{
			IComponentIdentifier	bid	= desc.getName()!=null ? desc.getName() : getScope().getComponentIdentifier();
			Date	leasetime	= lt==null ? desc.getLeaseTime() : new Date(getTime()+lt.longValue());
			desc	= new DFComponentDescription(bid, desc.getServices(), desc.getProtocols(), desc.getOntologies(), desc.getLanguages(), leasetime);
		}
		
		re.setComponentDescription(desc);

		IGoal req = createGoal("rp_initiate");
		req.getParameter("receiver").setValue(getParameter("df").getValue());
		req.getParameter("action").setValue(re);
		req.getParameter("ontology").setValue(SFipa.COMPONENT_MANAGEMENT_ONTOLOGY_NAME);
		dispatchSubgoalAndWait(req);

		getParameter("result").setValue(((DFRegister)((Done)req.getParameter("result").getValue()).getAction()).getResult());
	}
}
