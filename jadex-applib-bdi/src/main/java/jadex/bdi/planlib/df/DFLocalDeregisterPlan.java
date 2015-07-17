package jadex.bdi.planlib.df;

import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.df.IDF;
import jadex.bridge.service.types.df.IDFComponentDescription;
import jadex.commons.future.IFuture;


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
		IDF	dfservice	= (IDF)getAgent().getComponentFeature(IRequiredServicesFeature.class).getRequiredService("df").get();
		
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

		IFuture ret = dfservice.deregister(desc);
		ret.get();
	}
}
