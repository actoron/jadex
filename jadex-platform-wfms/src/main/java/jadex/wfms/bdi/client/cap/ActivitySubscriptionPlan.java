package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.IGoal;
import jadex.wfms.bdi.ontology.SubscribeActivityEvents;

public class ActivitySubscriptionPlan extends AbstractWfmsPlan
{
	public void body()
	{
		IGoal acSub = createGoal("subcap.sp_initiate");
		acSub.getParameter("receiver").setValue(getClientInterface());
		acSub.getParameter("subscription").setValue(new SubscribeActivityEvents());
		dispatchSubgoalAndWait(acSub);
	}
}
