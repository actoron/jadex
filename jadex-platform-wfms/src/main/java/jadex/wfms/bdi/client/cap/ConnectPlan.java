package jadex.wfms.bdi.client.cap;

import java.util.Set;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.wfms.bdi.ontology.RequestAuth;
import jadex.wfms.bdi.ontology.RequestCapabilities;

public class ConnectPlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestAuth reqAuth = new RequestAuth((String) getParameter("user_name").getValue(), getParameter("auth_token").getValue()); 
		
		IGoal authGoal = createGoal("reqcap.rp_initiate");
		authGoal.getParameter("action").setValue(reqAuth);
		authGoal.getParameter("receiver").setValue(getClientInterface());
		dispatchSubgoalAndWait(authGoal);
		
		IGoal hbGoal = createGoal("keep_sending_heartbeats");
		dispatchTopLevelGoal(hbGoal);
		
		RequestCapabilities rc = new RequestCapabilities();
		
		IGoal capGoal = createGoal("reqcap.rp_initiate");
		capGoal.getParameter("action").setValue(rc);
		capGoal.getParameter("receiver").setValue(getClientInterface());
		dispatchSubgoalAndWait(capGoal);
		Done done = (Done) capGoal.getParameter("result").getValue();
		Set capabilities = ((RequestCapabilities) done.getAction()).getCapabilities();
		getParameter("capabilities").setValue(capabilities);
	}
}
