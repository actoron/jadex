package jadex.wfms.bdi.interfaces.admin;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.SServiceProvider;
import jadex.wfms.UpdateSubscriptionStep;
import jadex.wfms.bdi.client.cap.AbstractWfmsPlan;
import jadex.wfms.bdi.ontology.InformUserActivityAdded;
import jadex.wfms.bdi.ontology.InformUserActivityRemoved;
import jadex.wfms.bdi.ontology.RequestProxy;
import jadex.wfms.client.IClient;
import jadex.wfms.listeners.ActivityEvent;
import jadex.wfms.listeners.IActivityListener;
import jadex.wfms.service.IAdministrationService;

public class StartUserActivitiesSubscriptionPlan extends AbstractWfmsPlan
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
		IActivityListener listener = new IActivityListener()
		{
			public void activityAdded(ActivityEvent event)
			{
				final InformUserActivityAdded update = new InformUserActivityAdded();
				update.setActivity(event.getActivity());
				update.setUserName(event.getUserName());
				
				agent.scheduleStep(new UpdateSubscriptionStep(subId, update));
			}
			
			public void activityRemoved(ActivityEvent event)
			{
				final InformUserActivityRemoved update = new InformUserActivityRemoved();
				update.setActivity(event.getActivity());
				update.setUserName(event.getUserName());
				
				agent.scheduleStep(new UpdateSubscriptionStep(subId, update));
			}
		};
		
		as.addActivitiesListener(proxy, listener);
	}
}
