package jadex.bdi.planlib.df;

import jadex.adapter.base.fipa.IDF;
import jadex.adapter.base.fipa.IDFAgentDescription;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IAgentIdentifier;

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
		IDFAgentDescription desc = (IDFAgentDescription)getParameter("description").getValue();

		// When AID is ommited, enter self. Hack???
		if(desc.getName()==null || lt!=null)
		{
			IDF	dfservice	= (IDF)getScope().getPlatform().getService(IDF.class, SFipa.DF_SERVICE);
			IAgentIdentifier	bid	= desc.getName()!=null ? desc.getName() : getScope().getAgentIdentifier();
			Date	leasetime	= lt==null ? desc.getLeaseTime() : new Date(getTime()+lt.longValue());
			desc	= dfservice.createDFAgentDescription(bid, desc.getServices(), desc.getLanguages(), desc.getOntologies(), desc.getProtocols(), leasetime);
		}

		getLogger().info("Trying to register: "+desc);

		SyncResultListener lis = new SyncResultListener();
		((IDF)getScope().getPlatform().getService(IDF.class, SFipa.DF_SERVICE)).register(desc, lis);
		// todo: supply return value or throw exception?
		desc = (IDFAgentDescription)lis.waitForResult();

		getParameter("result").setValue(desc);
	}
}
