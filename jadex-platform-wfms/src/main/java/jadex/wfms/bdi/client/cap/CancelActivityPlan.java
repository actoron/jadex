package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.IGoal;
import jadex.wfms.bdi.ontology.RequestCancelActivity;
import jadex.wfms.client.IClientActivity;

public class CancelActivityPlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestCancelActivity rca = new RequestCancelActivity();
		rca.setActivity((IClientActivity) getParameter("activity").getValue());
		
		IGoal caRequestGoal = createGoal("reqcap.rp_initiate");
		caRequestGoal.getParameter("action").setValue(rca);
		caRequestGoal.getParameter("receiver").setValue(getClientInterface());
		
		dispatchSubgoalAndWait(caRequestGoal);
	}

}
