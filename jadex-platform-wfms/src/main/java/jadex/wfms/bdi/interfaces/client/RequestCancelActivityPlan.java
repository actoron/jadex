package jadex.wfms.bdi.interfaces.client;

import jadex.adapter.base.fipa.Done;
import jadex.bdi.runtime.Plan;
import jadex.wfms.bdi.ontology.RequestBeginActivity;
import jadex.wfms.bdi.ontology.RequestCancelActivity;
import jadex.wfms.client.IClient;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.service.IClientService;

import java.security.AccessControlException;
import java.util.Map;

public class RequestCancelActivityPlan extends Plan
{
	public void body()
	{
		RequestCancelActivity rca = (RequestCancelActivity) getParameter("action").getValue();
		
		Map clientProxies = (Map) getBeliefbase().getBelief("client_proxies").getFact();
		IClient proxy = (IClient) clientProxies.get(getParameter("initiator").getValue());
		
		IClientService cs = (IClientService) getScope().getServiceContainer().getService(IClientService.class);
		
		try
		{
			cs.cancelActivity(proxy, rca.getActivity());
		}
		catch (AccessControlException e)
		{
			fail("Unauthorized Access!", e);
		}
		
		rca.setActivity(null);
		Done done = new Done();
		done.setAction(rca);
		getParameter("result").setValue(done);
	}
}
