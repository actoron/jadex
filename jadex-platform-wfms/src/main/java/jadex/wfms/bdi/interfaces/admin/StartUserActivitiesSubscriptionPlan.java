package jadex.wfms.bdi.interfaces.admin;

import jadex.adapter.base.fipa.Done;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bridge.IComponentIdentifier;
import jadex.wfms.bdi.client.cap.AbstractWfmsPlan;
import jadex.wfms.bdi.ontology.InformUserActivityAdded;
import jadex.wfms.bdi.ontology.InformUserActivityRemoved;
import jadex.wfms.bdi.ontology.RequestProxy;
import jadex.wfms.client.ActivityEvent;
import jadex.wfms.client.IActivityListener;
import jadex.wfms.client.IClient;
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
		IAdministrationService as = (IAdministrationService) getScope().getServiceContainer().getService(IAdministrationService.class);
		IActivityListener listener = new IActivityListener()
		{
			public void activityAdded(ActivityEvent event)
			{
				final InformUserActivityAdded update = new InformUserActivityAdded();
				update.setActivity(event.getActivity());
				update.setUserName(event.getUserName());
				
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
			
			public void activityRemoved(ActivityEvent event)
			{
				final InformUserActivityRemoved update = new InformUserActivityRemoved();
				update.setActivity(event.getActivity());
				update.setUserName(event.getUserName());
				
				agent.invokeLater(new Runnable()
				{
					public void run()
					{
						IGoal acRemoved = agent.createGoal("subcap.sp_submit_update");
						acRemoved.getParameter("update").setValue(update);
						acRemoved.getParameter("subscription_id").setValue(subId);
						agent.dispatchTopLevelGoal(acRemoved);
					}
				});
			}
		};
		
		as.addActivitiesListener(proxy, listener);
	}
}
