package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.IGoal;
import jadex.wfms.bdi.ontology.RequestProcessStart;

public class StartProcessPlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestProcessStart rps = new RequestProcessStart((String) getParameter("process_name").getValue());
		
		IGoal startGoal = createGoal("reqcap.rp_initiate");
		startGoal.getParameter("action").setValue(rps);
		startGoal.getParameter("receiver").setValue(getClientInterface());
		dispatchSubgoalAndWait(startGoal);
	}
}
