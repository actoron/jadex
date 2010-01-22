package jadex.wfms.bdi.interfaces.processdefinition;

import jadex.adapter.base.fipa.Done;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.wfms.bdi.client.cap.AbstractWfmsPlan;
import jadex.wfms.bdi.ontology.ComponentClientProxy;
import jadex.wfms.bdi.ontology.InformActivityAdded;
import jadex.wfms.bdi.ontology.InformActivityRemoved;
import jadex.wfms.bdi.ontology.InformProcessModelAdded;
import jadex.wfms.bdi.ontology.InformProcessModelRemoved;
import jadex.wfms.bdi.ontology.InformWorkitemAdded;
import jadex.wfms.bdi.ontology.InformWorkitemRemoved;
import jadex.wfms.bdi.ontology.RequestProxy;
import jadex.wfms.client.IClient;
import jadex.wfms.listeners.ActivityEvent;
import jadex.wfms.listeners.IActivityListener;
import jadex.wfms.listeners.IProcessRepositoryListener;
import jadex.wfms.listeners.IWorkitemListener;
import jadex.wfms.listeners.ProcessRepositoryEvent;
import jadex.wfms.listeners.WorkitemEvent;
import jadex.wfms.service.IClientService;
import jadex.wfms.service.IProcessDefinitionService;

import java.util.Map;

public class StartProcessRepositorySubscriptionPlan extends AbstractWfmsPlan
{
	public void body()
	{
		final Object subId = getParameter("subscription_id").getValue();
		
		IGoal acqProxy = createGoal("rp_initiate");
		acqProxy.getParameter("action").setValue(new RequestProxy((IComponentIdentifier) getParameter("initiator").getValue()));
		acqProxy.getParameter("receiver").setValue(getClientInterface());
		dispatchSubgoalAndWait(acqProxy);
		IClient proxy = ((RequestProxy) ((Done) acqProxy.getParameter("result").getValue()).getAction()).getClientProxy();
		if (proxy == null)
			fail();
		
		final IBDIExternalAccess agent = getExternalAccess();
		IProcessDefinitionService pds = (IProcessDefinitionService) getScope().getServiceContainer().getService(IProcessDefinitionService.class);
		IProcessRepositoryListener listener = new IProcessRepositoryListener()
		{
			public void processModelAdded(ProcessRepositoryEvent event)
			{
				final InformProcessModelAdded update = new InformProcessModelAdded();
				update.setModelName(event.getModelName());
				
				agent.invokeLater(new Runnable()
				{
					public void run()
					{
						IGoal acAdded = agent.createGoal("subcap.sp_submit_update");
						acAdded.getParameter("update").setValue(update);
						acAdded.getParameter("subscription_id").setValue(subId);
						agent.dispatchTopLevelGoal(acAdded);
					}
				});
			}
			
			public void processModelRemoved(ProcessRepositoryEvent event)
			{
				final InformProcessModelRemoved update = new InformProcessModelRemoved();
				update.setModelName(event.getModelName());
				
				agent.invokeLater(new Runnable()
				{
					public void run()
					{
						IGoal acAdded = agent.createGoal("subcap.sp_submit_update");
						acAdded.getParameter("update").setValue(update);
						acAdded.getParameter("subscription_id").setValue(subId);
						agent.dispatchTopLevelGoal(acAdded);
					}
				});
			}
		};
		
		pds.addProcessRepositoryListener(proxy, listener);
	}
}
