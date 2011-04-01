package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.Plan;
import jadex.wfms.service.IExternalWfmsService;

public class DisconnectPlan extends Plan
{
	public void body()
	{
		IExternalWfmsService wfms = (IExternalWfmsService) getBeliefbase().getBelief("wfms").getFact();
		wfms.deauthenticate(getComponentIdentifier());
		getBeliefbase().getBelief("wfms").setFact(null);
	}
}
