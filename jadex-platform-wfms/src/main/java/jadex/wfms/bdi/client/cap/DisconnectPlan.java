package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.IGoal;
import jadex.wfms.bdi.ontology.RequestDeAuth;

public class DisconnectPlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestDeAuth reqDeAuth = new RequestDeAuth((String) getParameter("user_name").getValue()); 
		
		while (getGoalbase().getGoals("keep_sending_heartbeats").length != 0)
		{
			IGoal goal = getGoalbase().getGoals("keep_sending_heartbeats")[0];
			goal.drop();
			waitForGoal(goal);
		}
		
		IGoal deAuthGoal = createGoal("reqcap.rp_initiate");
		deAuthGoal.getParameter("action").setValue(reqDeAuth);
		deAuthGoal.getParameter("receiver").setValue(getClientInterface());
		dispatchSubgoalAndWait(deAuthGoal);
	}
}
