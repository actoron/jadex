package jadex.wfms.bdi.interfaces.client;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.Plan;
import jadex.commons.service.SServiceProvider;
import jadex.wfms.UpdateSubscriptionStep;
import jadex.wfms.bdi.ontology.ComponentClientProxy;
import jadex.wfms.bdi.ontology.InformActivityAdded;
import jadex.wfms.bdi.ontology.InformActivityRemoved;
import jadex.wfms.listeners.ActivityEvent;
import jadex.wfms.listeners.IActivityListener;
import jadex.wfms.service.IClientService;

import java.util.Map;

public class StartActivitySubscriptionPlan extends Plan
{
	public void body()
	{
		final Object subId = getParameter("subscription_id").getValue();
		
		Map clientProxies = (Map) getBeliefbase().getBelief("client_proxies").getFact();
		final ComponentClientProxy proxy = (ComponentClientProxy) clientProxies.get(getParameter("initiator").getValue());
		if (proxy == null)
			fail();
		
		final IBDIExternalAccess agent = getExternalAccess();
		IClientService cs = (IClientService) SServiceProvider.getService(getScope().getServiceProvider(), IClientService.class).get(this);
		IActivityListener listener = new IActivityListener()
		{
			public void activityAdded(ActivityEvent event)
			{
				final InformActivityAdded update = new InformActivityAdded();
				update.setActivity(event.getActivity());
				
				agent.scheduleStep(new UpdateSubscriptionStep(subId, update));
			}
			
			public void activityRemoved(ActivityEvent event)
			{
				final InformActivityRemoved update = new InformActivityRemoved();
				update.setActivity(event.getActivity());
				
				agent.scheduleStep(new UpdateSubscriptionStep(subId, update));
			}
		};
		
		cs.addActivityListener(proxy, listener);
	}
}
