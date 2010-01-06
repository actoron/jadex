package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.IGoal;
import jadex.wfms.bdi.AbstractWfmsPlan;
import jadex.wfms.bdi.ontology.RequestFinishActivity;
import jadex.wfms.client.IClientActivity;

public class FinishActivityPlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestFinishActivity rfa = new RequestFinishActivity();
		rfa.setActivity((IClientActivity) getParameter("activity").getValue());
		
		IGoal faRequestGoal = createGoal("reqcap.rp_initiate");
		faRequestGoal.getParameter("action").setValue(rfa);
		faRequestGoal.getParameter("receiver").setValue(getClientInterface());
		
		dispatchSubgoalAndWait(faRequestGoal);
	}

}
