package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.Plan;
import jadex.wfms.service.IExternalWfmsService;

public class RemoveModelResourcePlan extends Plan
{
	public void body()
	{
		IExternalWfmsService wfms = (IExternalWfmsService) getBeliefbase().getBelief("wfms").getFact();
		wfms.removeProcessResource(getComponentIdentifier(), (String) getParameter("resource_name").getValue());
	}
}
