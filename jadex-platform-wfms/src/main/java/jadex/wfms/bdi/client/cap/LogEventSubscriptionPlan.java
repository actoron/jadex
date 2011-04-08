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
import jadex.wfms.service.listeners.ILogListener;
import jadex.wfms.service.listeners.LogEvent;

public class LogEventSubscriptionPlan extends Plan
{
	public void body()
	{
		IExternalWfmsService wfms = (IExternalWfmsService) getBeliefbase().getBelief("wfms").getFact();
		final IBDIExternalAccess ea = getScope().getExternalAccess();
		boolean pastevents = Boolean.TRUE.equals(getParameter("past_events"));
		wfms.addLogListener(getComponentIdentifier(), new ILogListener()
		{
			
			public IFuture logMessage(final LogEvent event)
			{
				return ea.scheduleStep(new IComponentStep()
				{
					public Object execute(IInternalAccess ia)
					{
						Future ret = new Future();
						IBDIInternalAccess bia = (IBDIInternalAccess) ia;
						IGoal ipfGoal = bia.getGoalbase().createGoal("handle_log_event");
						ipfGoal.getParameter("event").setValue(event);
						bia.getGoalbase().dispatchTopLevelGoal(ipfGoal);
						ret.setResult(null);
						return ret;
					}
				});
			}
		}, pastevents);
	}
}
