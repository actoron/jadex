package jadex.bdi.planlib.df;

import jadex.base.fipa.IDF;
import jadex.base.fipa.IDFComponentDescription;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.IFuture;

import java.util.Date;


/**
 *  Plan to register at the df.
 */
public class DFLocalRegisterPlan extends Plan
{
	/**
	 *  Plan body.
	 */
	public void body()
	{
		// Todo: support other parameters!?
		Number lt = (Number)getParameter("leasetime").getValue();
		IDFComponentDescription desc = (IDFComponentDescription)getParameter("description").getValue();

		// When AID is ommited, enter self. Hack???
		if(desc.getName()==null || lt!=null)
		{
			IDF	dfservice	= (IDF)getScope().getServiceContainer().getService(IDF.class);
			IComponentIdentifier	bid	= desc.getName()!=null ? desc.getName() : getScope().getComponentIdentifier();
			Date	leasetime	= lt==null ? desc.getLeaseTime() : new Date(getTime()+lt.longValue());
			desc	= dfservice.createDFComponentDescription(bid, desc.getServices(), desc.getLanguages(), desc.getOntologies(), desc.getProtocols(), leasetime);
		}

		getLogger().info("Trying to register: "+desc);

		SyncResultListener lis = new SyncResultListener();
		IFuture ret = ((IDF)getScope().getServiceContainer().getService(IDF.class)).register(desc);
		// todo: supply return value or throw exception?
		desc = (IDFComponentDescription)ret.get(this);

		getParameter("result").setValue(desc);
	}
}
