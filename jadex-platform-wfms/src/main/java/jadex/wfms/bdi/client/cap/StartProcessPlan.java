package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.Plan;
import jadex.wfms.service.IExternalWfmsService;

public class StartProcessPlan extends Plan
{
	public void body()
	{
		IExternalWfmsService wfms = (IExternalWfmsService) getBeliefbase().getBelief("wfms").getFact();
		wfms.startProcess(getComponentIdentifier(), (String) getParameter("process_name").getValue()).get(this);
	}
}
