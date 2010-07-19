package jadex.wfms.bdi.interfaces.client;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.Plan;
import jadex.service.SServiceProvider;
import jadex.wfms.bdi.ontology.RequestBeginActivity;
import jadex.wfms.client.IClient;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.service.IClientService;

import java.security.AccessControlException;
import java.util.Map;

public class RequestBeginActivityPlan extends Plan
{
	public void body()
	{
		RequestBeginActivity rba = (RequestBeginActivity) getParameter("action").getValue();
		
		Map clientProxies = (Map) getBeliefbase().getBelief("client_proxies").getFact();
		IClient proxy = (IClient) clientProxies.get(getParameter("initiator").getValue());
		
		IClientService cs = (IClientService) SServiceProvider.getService(getScope().getServiceProvider(), IClientService.class).get(this);
		
		try
		{
			cs.beginActivity(proxy, rba.getWorkitem());
		}
		catch (AccessControlException e)
		{
			fail("Unauthorized Access!", e);
		}
		
		Done done = new Done();
		done.setAction(rba);
		getParameter("result").setValue(done);
	}
}
