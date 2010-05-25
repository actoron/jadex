package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.IGoal;
import jadex.wfms.bdi.ontology.SubscribeProcessEvents;

public class ProcessEventSubscriptionPlan extends AbstractWfmsPlan
{
	public void body()
	{
		IGoal procSub = createGoal("subcap.sp_initiate");
		procSub.getParameter("receiver").setValue(getAdminInterface());
		procSub.getParameter("subscription").setValue(new SubscribeProcessEvents());
		dispatchSubgoalAndWait(procSub);
	}
}
