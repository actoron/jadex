package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.IGoal;
import jadex.wfms.bdi.ontology.SubscribeActivityEvents;
import jadex.wfms.bdi.ontology.SubscribeModelRepositoryEvents;

public class ModelRepositorySubscriptionPlan extends AbstractWfmsPlan
{
	public void body()
	{
		IGoal acSub = createGoal("subcap.sp_initiate");
		acSub.getParameter("receiver").setValue(getPdInterface());
		acSub.getParameter("subscription").setValue(new SubscribeModelRepositoryEvents());
		dispatchSubgoalAndWait(acSub);
	}
}
