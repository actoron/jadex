package jadex.wfms.bdi.interfaces.client;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.Plan;
import jadex.bridge.service.SServiceProvider;
import jadex.wfms.UpdateSubscriptionStep;
import jadex.wfms.bdi.ontology.ComponentClientProxy;
import jadex.wfms.bdi.ontology.InformWorkitemAdded;
import jadex.wfms.bdi.ontology.InformWorkitemRemoved;
import jadex.wfms.listeners.IWorkitemListener;
import jadex.wfms.listeners.WorkitemEvent;
import jadex.wfms.service.IClientService;

import java.util.Map;

public class StartWorkitemSubscriptionPlan extends Plan
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
		IWorkitemListener listener = new IWorkitemListener()
		{
			public void workitemRemoved(WorkitemEvent event)
			{
				final InformWorkitemRemoved update = new InformWorkitemRemoved();
				update.setWorkitem(event.getWorkitem());
				
				agent.scheduleStep(new UpdateSubscriptionStep(subId, update));
			}
			
			public void workitemAdded(WorkitemEvent event)
			{
				final InformWorkitemAdded update = new InformWorkitemAdded();
				update.setWorkitem(event.getWorkitem());
				
				agent.scheduleStep(new UpdateSubscriptionStep(subId, update));
			}
		};
		cs.addWorkitemListener(proxy, listener);
	}
}
