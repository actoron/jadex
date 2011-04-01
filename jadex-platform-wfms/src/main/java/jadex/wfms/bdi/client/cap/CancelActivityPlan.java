package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.Plan;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.service.IExternalWfmsService;

public class CancelActivityPlan extends Plan
{
	public void body()
	{
		IExternalWfmsService wfms = (IExternalWfmsService) getBeliefbase().getBelief("wfms").getFact();
		wfms.cancelActivity(getComponentIdentifier(), (IClientActivity) getParameter("activity").getValue());
	}

}
