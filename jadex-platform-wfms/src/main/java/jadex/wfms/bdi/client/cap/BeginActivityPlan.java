package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.IGoal;
import jadex.wfms.bdi.ontology.RequestBeginActivity;
import jadex.wfms.client.IWorkitem;

public class BeginActivityPlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestBeginActivity rba = new RequestBeginActivity();
		rba.setWorkitem((IWorkitem) getParameter("workitem").getValue());
		
		IGoal baRequestGoal = createGoal("reqcap.rp_initiate");
		baRequestGoal.getParameter("action").setValue(rba);
		baRequestGoal.getParameter("receiver").setValue(getClientInterface());
		
		dispatchSubgoalAndWait(baRequestGoal);
	}

}
