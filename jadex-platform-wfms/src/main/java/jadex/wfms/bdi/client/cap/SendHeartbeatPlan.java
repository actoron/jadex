package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.wfms.bdi.ontology.PerformHeartbeat;

public class SendHeartbeatPlan extends AbstractWfmsPlan
{
	public void body()
	{
		PerformHeartbeat phb = new PerformHeartbeat();
		
		IGoal hbGoal = createGoal("reqcap.rp_initiate");
		hbGoal.getParameter("action").setValue(phb);
		hbGoal.getParameter("receiver").setValue(getClientInterface());
		
		try
		{
			dispatchSubgoalAndWait(hbGoal);
		}
		catch (GoalFailureException e)
		{
		}
		
		if (!hbGoal.isSucceeded())
		{
			IGoal recoverGoal = createGoal("recover_lost_connection");
			dispatchTopLevelGoal(recoverGoal);
			((IGoal) getReason()).drop();
		}
	}
}
