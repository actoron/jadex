package jadex.bdi.planlib.df;

import java.util.Date;

import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.DFComponentDescription;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.df.IDF;

/**
 *  Convenience plan for legacy DF functionality.
 */
public class DFKeepRegisteredPlan	extends Plan
{
	public void body()
	{
		DFComponentDescription	desc	= (DFComponentDescription)getParameter("description").getValue();
		long	lease	= -1;
		if(desc.getLeaseTime()!=null)
		{
			lease	= desc.getLeaseTime().getTime()-getTime();
		}
		
		IDF	df = SServiceProvider.getLocalService(getAgent(), IDF.class, RequiredServiceInfo.SCOPE_PLATFORM);
		df.register(desc).get();
		while(lease!=-1)
		{
			waitFor((long)(lease*0.8));
			desc.setLeaseTime(new Date(getTime()+lease));
			df.modify(desc).get();
		}
	}
}
