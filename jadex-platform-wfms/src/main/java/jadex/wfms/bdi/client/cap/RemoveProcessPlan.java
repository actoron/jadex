package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.IGoal;
import jadex.wfms.bdi.ontology.RequestRemoveProcess;

public class RemoveProcessPlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestRemoveProcess rrp = new RequestRemoveProcess((String) getParameter("process_name").getValue());
		
		IGoal startGoal = createGoal("reqcap.rp_initiate");
		startGoal.getParameter("action").setValue(rrp);
		startGoal.getParameter("receiver").setValue(getPdInterface());
		dispatchSubgoalAndWait(startGoal);
	}
}
