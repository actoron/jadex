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

public class UserActivitiesSubscriptionPlan extends Plan
{
	public void body()
	{
		IExternalWfmsService wfms = (IExternalWfmsService) getBeliefbase().getBelief("wfms").getFact();
		final IBDIExternalAccess ea = getScope().getExternalAccess();
		wfms.addActivitiesListener(getComponentIdentifier(), new IActivityListener()
		{
			public IFuture activityRemoved(final ActivityEvent event)
			{
				return ea.scheduleStep(new IComponentStep()
				{
					public Object execute(IInternalAccess ia)
					{
						final Future ret = new Future();
						IBDIInternalAccess bia = (IBDIInternalAccess) ia;
						IGoal uacRemovedGoal = bia.getGoalbase().createGoal("remove_user_activity");
						uacRemovedGoal.getParameter("user_name").setValue(event.getUserName());
						uacRemovedGoal.getParameter("activity").setValue(event.getActivity());
						bia.getGoalbase().dispatchTopLevelGoal(uacRemovedGoal);
						ret.setResult(null);
						return ret;
					}
				});
			}
			
			public IFuture activityAdded(final ActivityEvent event)
			{
				return ea.scheduleStep(new IComponentStep()
				{
					public Object execute(IInternalAccess ia)
					{
						Future ret = new Future();
						IBDIInternalAccess bia = (IBDIInternalAccess) ia;
						IGoal uacAddedGoal = bia.getGoalbase().createGoal("add_user_activity");
						uacAddedGoal.getParameter("user_name").setValue(event.getUserName());
						uacAddedGoal.getParameter("activity").setValue(event.getActivity());
						bia.getGoalbase().dispatchTopLevelGoal(uacAddedGoal);
						ret.setResult(null);
						return ret;
					}
				});
			}
		});
	}
}
