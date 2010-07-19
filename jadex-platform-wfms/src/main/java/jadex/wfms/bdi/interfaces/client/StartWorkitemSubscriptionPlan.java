package jadex.wfms.bdi.interfaces.client;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.service.SServiceProvider;
import jadex.wfms.GoalDispatchResultListener;
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
		IClientService cs = (IClientService) SServiceProvider.getService(getScope().getServiceProvider(), IClientService.class).get(this);
		IWorkitemListener listener = new IWorkitemListener()
		{
			public void workitemRemoved(WorkitemEvent event)
			{
				final InformWorkitemRemoved update = new InformWorkitemRemoved();
				update.setWorkitem(event.getWorkitem());
				
				agent.createGoal("subcap.sp_submit_update").addResultListener(new GoalDispatchResultListener(agent)
				{
					public void configureGoal(jadex.bdi.runtime.IEAGoal goal)
					{
						goal.setParameterValue("update", update);
						goal.setParameterValue("subscription_id", subId);
					}
				});
			}
			
			public void workitemAdded(WorkitemEvent event)
			{
				final InformWorkitemAdded update = new InformWorkitemAdded();
				update.setWorkitem(event.getWorkitem());
				
				agent.createGoal("subcap.sp_submit_update").addResultListener(new GoalDispatchResultListener(agent)
				{
					public void configureGoal(jadex.bdi.runtime.IEAGoal goal)
					{
						goal.setParameterValue("update", update);
						goal.setParameterValue("subscription_id", subId);
					}
				});
			}
		};
		
		cs.addWorkitemListener(proxy, listener);
	}
}
