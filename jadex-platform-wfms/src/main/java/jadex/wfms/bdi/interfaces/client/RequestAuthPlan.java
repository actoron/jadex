package jadex.wfms.bdi.interfaces.client;

import java.util.Map;

import jadex.base.fipa.Done;
import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.wfms.bdi.ontology.ComponentClientProxy;
import jadex.wfms.bdi.ontology.RequestAuth;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IClientService;

public class RequestAuthPlan extends Plan
{
	public void body()
	{
		RequestAuth ra = (RequestAuth) getParameter("action").getValue();
		
		ComponentClientProxy proxy = new ComponentClientProxy(ra.getUserName(), (IComponentIdentifier) getParameter("initiator").getValue());
		
		IClientService cs = (IClientService) getScope().getServiceProvider().getService(IClientService.class);
		if (cs.authenticate(proxy))
		{
			Map clientProxies = (Map) getBeliefbase().getBelief("client_proxies").getFact();
			clientProxies.put(proxy.getComponentIdentifier(), proxy);
			Map heartbeatTimers = (Map) getBeliefbase().getBelief("heartbeat_timers").getFact();
			heartbeatTimers.put(proxy.getComponentIdentifier(), new Long(System.currentTimeMillis()));
			Done done = new Done();
			done.setAction(ra);
			getParameter("result").setValue(done);
		}
		else
		{
			fail("Authentication failed!", null);
		}
	}
}
