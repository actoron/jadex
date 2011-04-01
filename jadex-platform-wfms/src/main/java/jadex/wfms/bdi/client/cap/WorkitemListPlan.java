package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.Plan;
import jadex.wfms.service.IExternalWfmsService;

public class WorkitemListPlan extends Plan
{
	public void body()
	{
		IExternalWfmsService wfms = (IExternalWfmsService) getBeliefbase().getBelief("wfms").getFact();
		getParameter("workitem_list").setValue(wfms.getAvailableWorkitems(getComponentIdentifier()).get(this));
	}

}
