package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.Plan;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.service.IExternalWfmsService;

public class FinishActivityPlan extends Plan
{
	public void body()
	{
		IExternalWfmsService wfms = (IExternalWfmsService) getBeliefbase().getBelief("wfms").getFact();
		wfms.finishActivity(getComponentIdentifier(), (IClientActivity) getParameter("activity").getValue());
	}

}
