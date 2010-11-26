package jadex.wfms.bdi.interfaces.processdefinition;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.service.SServiceProvider;
import jadex.wfms.UpdateSubscriptionStep;
import jadex.wfms.bdi.client.cap.AbstractWfmsPlan;
import jadex.wfms.bdi.ontology.InformProcessModelAdded;
import jadex.wfms.bdi.ontology.InformProcessModelRemoved;
import jadex.wfms.bdi.ontology.RequestProxy;
import jadex.wfms.client.IClient;
import jadex.wfms.listeners.IProcessRepositoryListener;
import jadex.wfms.listeners.ProcessRepositoryEvent;
import jadex.wfms.service.IProcessDefinitionService;

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
		IProcessDefinitionService pds = (IProcessDefinitionService) SServiceProvider.getService(getScope().getServiceProvider(), IProcessDefinitionService.class).get(this);
		IProcessRepositoryListener listener = new IProcessRepositoryListener()
		{
			public void processModelAdded(ProcessRepositoryEvent event)
			{
				final InformProcessModelAdded update = new InformProcessModelAdded();
				update.setModelName(event.getModelName());
				
				agent.scheduleStep(new UpdateSubscriptionStep(subId, update));
			}
			
			public void processModelRemoved(ProcessRepositoryEvent event)
			{
				final InformProcessModelRemoved update = new InformProcessModelRemoved();
				update.setModelName(event.getModelName());
				
				agent.scheduleStep(new UpdateSubscriptionStep(subId, update));
			}
		};
		
		pds.addProcessRepositoryListener(proxy, listener);
	}
}
