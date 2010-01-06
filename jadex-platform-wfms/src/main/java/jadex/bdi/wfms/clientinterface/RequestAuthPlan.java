package jadex.bdi.wfms.clientinterface;

import java.util.Map;

import jadex.adapter.base.fipa.Done;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bdi.wfms.ontology.ComponentClientProxy;
import jadex.bdi.wfms.ontology.RequestAuth;
import jadex.bridge.IComponentIdentifier;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IClientService;

public class RequestAuthPlan extends Plan
{
	public void body()
	{
		RequestAuth ra = (RequestAuth) getParameter("action").getValue();
		
		ComponentClientProxy proxy = new ComponentClientProxy(ra.getUserName(), (IComponentIdentifier) getParameter("initiator").getValue());
		
		IClientService cs = (IClientService) getScope().getServiceContainer().getService(IClientService.class);
		if (cs.authenticate(proxy))
		{
			Map clientProxies = (Map) getBeliefbase().getBelief("client_proxies").getFact();
			clientProxies.put(proxy.getComponentIdentifier(), proxy);
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
