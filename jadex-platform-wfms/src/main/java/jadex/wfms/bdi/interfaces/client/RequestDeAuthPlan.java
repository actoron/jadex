package jadex.wfms.bdi.interfaces.client;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.Plan;
import jadex.wfms.bdi.ontology.RequestDeAuth;
import jadex.wfms.client.IClient;
import jadex.wfms.listeners.IWorkitemListener;
import jadex.wfms.service.IClientService;

import java.util.Map;

public class RequestDeAuthPlan extends Plan
{
	public void body()
	{
		Map clientProxies = (Map) getBeliefbase().getBelief("client_proxies").getFact();
		IClient proxy = (IClient) clientProxies.remove(getParameter("initiator").getValue());
		Map heartbeatTimers = (Map) getBeliefbase().getBelief("heartbeat_timers").getFact();
		heartbeatTimers.remove(getParameter("initiator").getValue());
		
		IClientService cs = (IClientService) getScope().getServiceContainer().getService(IClientService.class);
		cs.deauthenticate(proxy);
		
		Done done = new Done();
		done.setAction((RequestDeAuth) getParameter("action").getValue());
		getParameter("result").setValue(done);
	}
}
