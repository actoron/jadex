package jadex.wfms.bdi.clientinterface;

import jadex.adapter.base.fipa.Done;
import jadex.bdi.runtime.Plan;
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
		
		IClientService cs = (IClientService) getScope().getServiceContainer().getService(IClientService.class);
		
		IClientActivity activity = null;
		try
		{
			activity = cs.beginActivity(proxy, rba.getWorkitem());
		}
		catch (AccessControlException e)
		{
			fail("Unauthorized Access!", e);
		}
		
		rba.setActivity(activity);
		Done done = new Done();
		done.setAction(rba);
		getParameter("result").setValue(done);
	}
}
