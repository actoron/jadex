package jadex.bdi.planlib.df;

import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.df.IDF;
import jadex.bridge.service.types.df.IDFComponentDescription;
import jadex.commons.future.IFuture;

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
			IDF	dfservice	= (IDF)getAgent().getComponentFeature(IRequiredServicesFeature.class).getRequiredService("df").get();
			IComponentIdentifier	bid	= desc.getName()!=null ? desc.getName() : getScope().getComponentIdentifier();
			Date	leasetime	= lt==null ? desc.getLeaseTime() : new Date(getTime()+lt.longValue());
			desc	= dfservice.createDFComponentDescription(bid, desc.getServices(), desc.getLanguages(), desc.getOntologies(), desc.getProtocols(), leasetime);
		}

		getLogger().info("Trying to register: "+desc);

		IFuture ret = ((IDF)getAgent().getComponentFeature(IRequiredServicesFeature.class).getRequiredService("df").get()).register(desc);
		// todo: supply return value or throw exception?
		desc = (IDFComponentDescription)ret.get();

		getParameter("result").setValue(desc);
	}
}
