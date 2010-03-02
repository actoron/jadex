package jadex.wfms.bdi.interfaces.client;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.Plan;
import jadex.wfms.bdi.ontology.RequestFinishActivity;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IClientService;

import java.security.AccessControlException;
import java.util.Map;

public class RequestFinishActivityPlan extends Plan
{
	public void body()
	{
		RequestFinishActivity rfa = (RequestFinishActivity) getParameter("action").getValue();
		
		Map clientProxies = (Map) getBeliefbase().getBelief("client_proxies").getFact();
		IClient proxy = (IClient) clientProxies.get(getParameter("initiator").getValue());
		
		IClientService cs = (IClientService) getScope().getServiceContainer().getService(IClientService.class);
		
		try
		{
			cs.finishActivity(proxy, rfa.getActivity());
		}
		catch (AccessControlException e)
		{
			fail("Unauthorized Access!", e);
		}
		
		rfa.setActivity(null);
		Done done = new Done();
		done.setAction(rfa);
		getParameter("result").setValue(done);
	}
}
