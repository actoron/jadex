package jadex.wfms.bdi.interfaces.client;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.Plan;
import jadex.commons.service.SServiceProvider;
import jadex.wfms.bdi.ontology.RequestCancelActivity;
import jadex.wfms.client.IClient;
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
		
		IClientService cs = (IClientService) SServiceProvider.getService(getScope().getServiceProvider(), IClientService.class).get(this);
		
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
