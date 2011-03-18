package jadex.wfms.bdi.interfaces.admin;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.SServiceProvider;
import jadex.wfms.UpdateSubscriptionStep;
import jadex.wfms.bdi.client.cap.AbstractWfmsPlan;
import jadex.wfms.bdi.ontology.InformProcessFinished;
import jadex.wfms.bdi.ontology.RequestProxy;
import jadex.wfms.client.IClient;
import jadex.wfms.listeners.IProcessListener;
import jadex.wfms.listeners.ProcessEvent;
import jadex.wfms.service.IAdministrationService;

public class StartProcessEventSubscriptionPlan extends AbstractWfmsPlan
{
	public void body()
	{
		final Object subId = getParameter("subscription_id").getValue();
		
		IGoal acqProxy = createGoal("rp_initiate");
		acqProxy.getParameter("action").setValue(new RequestProxy((IComponentIdentifier) getParameter("initiator").getValue()));
		acqProxy.getParameter("receiver").setValue(getClientInterface());
		dispatchSubgoalAndWait(acqProxy);
		IClient proxy = ((RequestProxy) ((Done) acqProxy.getParameter("result").getValue()).getAction()).getClientProxy();
		
		final IBDIExternalAccess agent = getExternalAccess();
		IAdministrationService as = (IAdministrationService) SServiceProvider.getService(getScope().getServiceProvider(), IAdministrationService.class).get(this);
		IProcessListener listener = new IProcessListener()
		{
			public void processFinished(ProcessEvent event)
			{
				final InformProcessFinished update = new InformProcessFinished();
				update.setInstanceId(event.getInstanceId());
				
				agent.scheduleStep(new UpdateSubscriptionStep(subId, update));
			}
		};
		
		as.addProcessListener(proxy, listener);
	}
}
