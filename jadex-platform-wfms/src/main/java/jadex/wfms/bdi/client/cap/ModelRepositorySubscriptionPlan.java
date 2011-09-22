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
import jadex.wfms.service.listeners.IProcessRepositoryListener;
import jadex.wfms.service.listeners.ProcessRepositoryEvent;

public class ModelRepositorySubscriptionPlan extends Plan
{
	public void body()
	{
		IExternalWfmsService wfms = (IExternalWfmsService) getBeliefbase().getBelief("wfms").getFact();
		final IBDIExternalAccess ea = getScope().getExternalAccess();
		wfms.addProcessRepositoryListener(getComponentIdentifier(), new IProcessRepositoryListener()
		{
			public IFuture processModelRemoved(final ProcessRepositoryEvent event)
			{
				return ea.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						IBDIInternalAccess bia = (IBDIInternalAccess) ia;
						IGoal pmAddedGoal = bia.getGoalbase().createGoal("remove_process_model");
						pmAddedGoal.getParameter("model_name").setValue(event.getModelName());
						bia.getGoalbase().dispatchTopLevelGoal(pmAddedGoal);
						return IFuture.DONE;
					}
				});
			}
			
			public IFuture processModelAdded(final ProcessRepositoryEvent event)
			{
				return ea.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						IBDIInternalAccess bia = (IBDIInternalAccess) ia;
						IGoal pmRemovedGoal = bia.getGoalbase().createGoal("add_process_model");
						pmRemovedGoal.getParameter("model_name").setValue(event.getModelName());
						bia.getGoalbase().dispatchTopLevelGoal(pmRemovedGoal);
						return IFuture.DONE;
					}
				});
			}
		});
	}
}
