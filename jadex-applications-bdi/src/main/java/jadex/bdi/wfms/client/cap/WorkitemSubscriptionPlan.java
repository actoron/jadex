package jadex.bdi.wfms.client.cap;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.wfms.AbstractWfmsPlan;
import jadex.bdi.wfms.ontology.SubscribeWorkitemEvents;

public class WorkitemSubscriptionPlan extends AbstractWfmsPlan
{
	public void body()
	{
		IGoal wiSub = createGoal("subcap.sp_initiate");
		wiSub.getParameter("receiver").setValue(getClientInterface());
		wiSub.getParameter("subscription").setValue(new SubscribeWorkitemEvents());
		dispatchSubgoalAndWait(wiSub);
	}
}
