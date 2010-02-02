package jadex.bdi.planlib.df;

import jadex.adapter.base.fipa.IDF;
import jadex.adapter.base.fipa.IDFComponentDescription;
import jadex.bdi.runtime.Plan;


/**
 *  Plan to deregister at the df.
 */
public class DFLocalDeregisterPlan extends Plan
{
	/**
	 *  Plan body.
	 */
	public void body()
	{
		IDF	dfservice	= (IDF)getScope().getServiceContainer().getService(IDF.class);
		
		// In case of a remote request the agent description is already
		// set via the remote deregister plan.
		IDFComponentDescription desc = (IDFComponentDescription)getParameter("description").getValue();
		if(desc==null)
		{
			desc = dfservice.createDFComponentDescription(getScope().getComponentIdentifier(), null);
		}
		else if(desc.getName()==null)
		{
			desc = dfservice.createDFComponentDescription(getScope().getComponentIdentifier(), desc.getServices(), desc.getLanguages(), 
				desc.getOntologies(), desc.getProtocols(), desc.getLeaseTime());
		}

		SyncResultListener lis = new SyncResultListener();
		dfservice.deregister(desc, lis);
		lis.waitForResult();
	}
}
