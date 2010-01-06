package jadex.bdi.wfms.clientinterface;

import jadex.adapter.base.fipa.Done;
import jadex.bdi.runtime.Plan;
import jadex.bdi.wfms.ontology.RequestProxy;
import jadex.wfms.client.IClient;

import java.util.Map;

public class RequestProxyPlan extends Plan
{
	public void body()
	{
		RequestProxy rp = (RequestProxy) getParameter("action").getValue();
		
		Map clientProxies = (Map) getBeliefbase().getBelief("client_proxies").getFact();
		rp.setClientProxy((IClient) clientProxies.get(rp.getComponentIdentifier()));
		
		Done done = new Done();
		done.setAction(rp);
		getParameter("result").setValue(done);
	}
}
