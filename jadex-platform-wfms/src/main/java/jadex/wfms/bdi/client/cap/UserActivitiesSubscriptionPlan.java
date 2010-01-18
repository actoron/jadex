package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.IGoal;
import jadex.wfms.bdi.ontology.SubscribeUserActivitiesEvents;

public class UserActivitiesSubscriptionPlan extends AbstractWfmsPlan
{
	public void body()
	{
		IGoal wiSub = createGoal("subcap.sp_initiate");
		wiSub.getParameter("receiver").setValue(getAdminInterface());
		wiSub.getParameter("subscription").setValue(new SubscribeUserActivitiesEvents());
		dispatchSubgoalAndWait(wiSub);
	}
}
