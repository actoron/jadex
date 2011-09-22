package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.wfms.service.IExternalWfmsService;
import jadex.wfms.service.listeners.ActivityEvent;
import jadex.wfms.service.listeners.IActivityListener;

public class ActivitySubscriptionPlan extends Plan
{
	public void body()
	{
		IExternalWfmsService wfms = (IExternalWfmsService) getBeliefbase().getBelief("wfms").getFact();
		final IBDIExternalAccess ea = getScope().getExternalAccess();
		wfms.addActivityListener(getComponentIdentifier(), new IActivityListener()
		{
			public IFuture activityRemoved(final ActivityEvent event)
			{
				return ea.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						IBDIInternalAccess bia = (IBDIInternalAccess) ia;
						IGoal acRemovedGoal = bia.getGoalbase().createGoal("remove_activity");
						acRemovedGoal.getParameter("activity").setValue(event.getActivity());
						bia.getGoalbase().dispatchTopLevelGoal(acRemovedGoal);
						return IFuture.DONE;
					}
				});
			}
			
			public IFuture activityAdded(final ActivityEvent event)
			{
				return ea.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						IBDIInternalAccess bia = (IBDIInternalAccess) ia;
						IGoal acAddedGoal = bia.getGoalbase().createGoal("add_activity");
						acAddedGoal.getParameter("activity").setValue(event.getActivity());
						bia.getGoalbase().dispatchTopLevelGoal(acAddedGoal);
						return IFuture.DONE;
					}
				});
			}
		});
	}
}
