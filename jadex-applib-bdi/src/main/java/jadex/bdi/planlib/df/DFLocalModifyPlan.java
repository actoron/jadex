package jadex.bdi.planlib.df;

import jadex.adapter.base.fipa.IDF;
import jadex.adapter.base.fipa.IDFAgentDescription;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;

import java.util.Date;


/**
 *  Plan to register at the df.
 */
public class DFLocalModifyPlan extends Plan
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
			IDF	dfservice	= (IDF)getScope().getServiceContainer().getService(IDF.class, SFipa.DF_SERVICE);
			IComponentIdentifier	bid	= desc.getName()!=null ? desc.getName() : getScope().getAgentIdentifier();
			Date	leasetime	= lt==null ? desc.getLeaseTime() : new Date(getTime()+lt.longValue());
			desc	= dfservice.createDFAgentDescription(bid, desc.getServices(), desc.getLanguages(), desc.getOntologies(), desc.getProtocols(), leasetime);
		}

		getLogger().info("Trying to modify: "+desc);

		// Throws exception, when not registered.
		SyncResultListener lis = new SyncResultListener();
		try
		{
			((IDF)getScope().getServiceContainer().getService(IDF.class, SFipa.DF_SERVICE)).modify(desc, lis);
			lis.waitForResult();
		}
		catch(Exception e)
		{
			fail();
		}
		
		// Todo: Need to use clone to avoid setting the same object causing no effect :-(
		getParameter("result").setValue(desc);
	}
}
