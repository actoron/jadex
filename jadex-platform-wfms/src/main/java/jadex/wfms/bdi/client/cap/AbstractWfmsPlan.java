package jadex.wfms.bdi.client.cap;

import jadex.adapter.base.fipa.IDF;
import jadex.adapter.base.fipa.IDFAgentDescription;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.wfms.bdi.ontology.SWfms;

public abstract class AbstractWfmsPlan extends Plan
{
	protected IComponentIdentifier getClientInterface()
	{
		IDF df = (IDF) getScope().getServiceContainer().getService(IDF.class);
		IDFAgentDescription adesc = df.createDFAgentDescription(null, df.createDFServiceDescription(SWfms.SERVICE_NAME_CLIENT_INTERFACE, SWfms.SERVICE_TYPE_EXTERNAL, null));
		
		return getWfmsService(adesc);
	}
	
	protected IComponentIdentifier getPdInterface()
	{
		IDF df = (IDF) getScope().getServiceContainer().getService(IDF.class);
		IDFAgentDescription adesc = df.createDFAgentDescription(null, df.createDFServiceDescription(SWfms.SERVICE_NAME_PD_INTERFACE, SWfms.SERVICE_TYPE_EXTERNAL, null));
		
		return getWfmsService(adesc);
	}
	
	protected IComponentIdentifier getAdminInterface()
	{
		IDF df = (IDF) getScope().getServiceContainer().getService(IDF.class);
		IDFAgentDescription adesc = df.createDFAgentDescription(null, df.createDFServiceDescription(SWfms.SERVICE_NAME_ADMIN_INTERFACE, SWfms.SERVICE_TYPE_EXTERNAL, null));
		
		return getWfmsService(adesc);
	}
	
	private IComponentIdentifier getWfmsService(IDFAgentDescription adesc)
	{
		IGoal dfGoal = createGoal("dfcap.df_search");
		dfGoal.getParameter("description").setValue(adesc);
		dispatchSubgoalAndWait(dfGoal);
		adesc = (IDFAgentDescription) dfGoal.getParameterSet("result").getValues()[0];
		return adesc.getName();
	}
}
