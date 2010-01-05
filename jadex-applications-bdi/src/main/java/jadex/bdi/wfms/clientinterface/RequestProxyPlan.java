package jadex.bdi.wfms.clientinterface;

import java.util.Map;

import jadex.adapter.base.fipa.Done;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.examples.cleanerworld_classic.RequestCompleteVision;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bdi.wfms.ontology.RequestProxy;
import jadex.wfms.client.IClient;

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
