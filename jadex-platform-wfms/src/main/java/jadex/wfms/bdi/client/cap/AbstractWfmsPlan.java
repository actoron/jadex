package jadex.wfms.bdi.client.cap;

import jadex.base.fipa.IDF;
import jadex.base.fipa.IDFComponentDescription;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.wfms.bdi.ontology.SWfms;

public abstract class AbstractWfmsPlan extends Plan
{
	protected IComponentIdentifier getClientInterface()
	{
		IDF df = (IDF) SServiceProvider.getService(getScope().getServiceProvider(), IDF.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);
		IDFComponentDescription adesc = df.createDFComponentDescription(null, df.createDFServiceDescription(SWfms.SERVICE_NAME_CLIENT_INTERFACE, SWfms.SERVICE_TYPE_EXTERNAL, null));
		
		return getWfmsService(adesc);
	}
	
	protected IComponentIdentifier getPdInterface()
	{
		IDF df = (IDF) SServiceProvider.getService(getScope().getServiceProvider(), IDF.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);
		IDFComponentDescription adesc = df.createDFComponentDescription(null, df.createDFServiceDescription(SWfms.SERVICE_NAME_PD_INTERFACE, SWfms.SERVICE_TYPE_EXTERNAL, null));
		
		return getWfmsService(adesc);
	}
	
	protected IComponentIdentifier getAdminInterface()
	{
		IDF df = (IDF) SServiceProvider.getService(getScope().getServiceProvider(), IDF.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);
		IDFComponentDescription adesc = df.createDFComponentDescription(null, df.createDFServiceDescription(SWfms.SERVICE_NAME_ADMIN_INTERFACE, SWfms.SERVICE_TYPE_EXTERNAL, null));
		
		return getWfmsService(adesc);
	}
	
	private IComponentIdentifier getWfmsService(IDFComponentDescription adesc)
	{
		IGoal dfGoal = createGoal("dfcap.df_search");
		dfGoal.getParameter("description").setValue(adesc);
		dispatchSubgoalAndWait(dfGoal);
		adesc = (IDFComponentDescription) dfGoal.getParameterSet("result").getValues()[0];
		return adesc.getName();
	}
}
