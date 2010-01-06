package jadex.bdi.wfms.client.cap;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.wfms.AbstractWfmsPlan;
import jadex.bdi.wfms.ontology.RequestAuth;

public class ConnectPlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestAuth reqAuth = new RequestAuth((String) getParameter("user_name").getValue(), getParameter("auth_token").getValue()); 
		
		IGoal authGoal = createGoal("reqcap.rp_initiate");
		authGoal.getParameter("action").setValue(reqAuth);
		authGoal.getParameter("receiver").setValue(getClientInterface());
		dispatchSubgoalAndWait(authGoal);
	}
}
