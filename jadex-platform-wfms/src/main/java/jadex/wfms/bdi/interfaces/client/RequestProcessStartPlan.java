package jadex.wfms.bdi.interfaces.client;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.Plan;
import jadex.commons.service.SServiceProvider;
import jadex.wfms.bdi.ontology.RequestProcessStart;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IClientService;

import java.security.AccessControlException;
import java.util.Map;

public class RequestProcessStartPlan extends Plan
{
	public void body()
	{
		RequestProcessStart rps = (RequestProcessStart) getParameter("action").getValue();
		
		Map clientProxies = (Map) getBeliefbase().getBelief("client_proxies").getFact();
		IClient proxy = (IClient) clientProxies.get(getParameter("initiator").getValue());
		
		IClientService cs = (IClientService) SServiceProvider.getService(getScope().getServiceProvider(), IClientService.class).get(this);
		
		try
		{
			cs.startProcess(proxy, rps.getProcessName());
		}
		catch (AccessControlException e)
		{
			fail("Unauthorized Access!", e);
		}
		
		Done done = new Done();
		done.setAction(rps);
		getParameter("result").setValue(done);
	}
}
