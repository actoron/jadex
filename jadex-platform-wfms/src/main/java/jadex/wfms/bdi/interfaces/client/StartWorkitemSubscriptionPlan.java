package jadex.wfms.bdi.interfaces.client;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.wfms.bdi.ontology.ComponentClientProxy;
import jadex.wfms.bdi.ontology.InformActivityAdded;
import jadex.wfms.bdi.ontology.InformActivityRemoved;
import jadex.wfms.bdi.ontology.InformWorkitemAdded;
import jadex.wfms.bdi.ontology.InformWorkitemRemoved;
import jadex.wfms.client.IClient;
import jadex.wfms.listeners.ActivityEvent;
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
		IClientService cs = (IClientService) getScope().getServiceProvider().getService(IClientService.class);
		IWorkitemListener listener = new IWorkitemListener()
		{
			public void workitemRemoved(WorkitemEvent event)
			{
				final InformWorkitemRemoved update = new InformWorkitemRemoved();
				update.setWorkitem(event.getWorkitem());
				
				agent.invokeLater(new Runnable()
				{
					public void run()
					{
						IGoal wiRemoved = agent.createGoal("subcap.sp_submit_update");
						wiRemoved.getParameter("update").setValue(update);
						wiRemoved.getParameter("subscription_id").setValue(subId);
						agent.dispatchTopLevelGoal(wiRemoved);
					}
				});
			}
			
			public void workitemAdded(WorkitemEvent event)
			{
				final InformWorkitemAdded update = new InformWorkitemAdded();
				update.setWorkitem(event.getWorkitem());
				
				agent.invokeLater(new Runnable()
				{
					public void run()
					{
						IGoal wiAdded = agent.createGoal("subcap.sp_submit_update");
						wiAdded.getParameter("update").setValue(update);
						wiAdded.getParameter("subscription_id").setValue(subId);
						agent.dispatchTopLevelGoal(wiAdded);
					}
				});
			}
		};
		
		cs.addWorkitemListener(proxy, listener);
	}
}
