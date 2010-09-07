package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.IGoal;
import jadex.wfms.bdi.ontology.RequestTerminateActivity;
import jadex.wfms.client.IClientActivity;

public class TerminateActivityPlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestTerminateActivity rta = new RequestTerminateActivity();
		rta.setActivity((IClientActivity) getParameter("activity").getValue());
		
		IGoal taRequestGoal = createGoal("reqcap.rp_initiate");
		taRequestGoal.getParameter("action").setValue(rta);
		taRequestGoal.getParameter("receiver").setValue(getAdminInterface());
		
		dispatchSubgoalAndWait(taRequestGoal);
	}

}
