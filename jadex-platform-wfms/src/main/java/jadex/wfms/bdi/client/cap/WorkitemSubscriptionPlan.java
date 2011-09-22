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
import jadex.wfms.service.listeners.IWorkitemListener;
import jadex.wfms.service.listeners.WorkitemEvent;

public class WorkitemSubscriptionPlan extends Plan
{
	public void body()
	{
		IExternalWfmsService wfms = (IExternalWfmsService) getBeliefbase().getBelief("wfms").getFact();
		final IBDIExternalAccess ea = getScope().getExternalAccess();
		wfms.addWorkitemListener(getComponentIdentifier(), new IWorkitemListener()
		{
			
			public IFuture workitemRemoved(final WorkitemEvent event)
			{
				return ea.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						IBDIInternalAccess bia = (IBDIInternalAccess) ia;
						IGoal acRemovedGoal = bia.getGoalbase().createGoal("remove_workitem");
						acRemovedGoal.getParameter("workitem").setValue(event.getWorkitem());
						bia.getGoalbase().dispatchTopLevelGoal(acRemovedGoal);
						return IFuture.DONE;
					}
				});
			}
			
			public IFuture workitemAdded(final WorkitemEvent event)
			{
				return ea.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						IBDIInternalAccess bia = (IBDIInternalAccess) ia;
						IGoal acAddedGoal = bia.getGoalbase().createGoal("add_workitem");
						acAddedGoal.getParameter("workitem").setValue(event.getWorkitem());
						bia.getGoalbase().dispatchTopLevelGoal(acAddedGoal);
						return IFuture.DONE;
					}
				});
			}
		});
	}
}
