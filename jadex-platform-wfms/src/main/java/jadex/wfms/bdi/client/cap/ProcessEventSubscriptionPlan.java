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
import jadex.wfms.service.listeners.IProcessListener;
import jadex.wfms.service.listeners.ProcessEvent;

public class ProcessEventSubscriptionPlan extends Plan
{
	public void body()
	{
		IExternalWfmsService wfms = (IExternalWfmsService) getBeliefbase().getBelief("wfms").getFact();
		final IBDIExternalAccess ea = getScope().getExternalAccess();
		wfms.addProcessListener(getComponentIdentifier(), new IProcessListener()
		{
			public IFuture processFinished(final ProcessEvent event)
			{
				return ea.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						IBDIInternalAccess bia = (IBDIInternalAccess) ia;
						IGoal ipfGoal = bia.getGoalbase().createGoal("handle_process_finished");
						ipfGoal.getParameter("instance_id").setValue(event.getInstanceId());
						bia.getGoalbase().dispatchTopLevelGoal(ipfGoal);
						return IFuture.DONE;
					}
				});
			}
		});
	}
}
