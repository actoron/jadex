package jadex.wfms.bdi.interfaces.client;

import jadex.base.fipa.Done;
import jadex.bridge.service.SServiceProvider;
import jadex.wfms.bdi.client.cap.AbstractWfmsPlan;
import jadex.wfms.bdi.ontology.RequestCapabilities;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IClientService;

import java.util.Map;
import java.util.Set;

public class RequestCapabilitiesPlan extends AbstractWfmsPlan
{
	public void body()
	{
		Map clientProxies = (Map) getBeliefbase().getBelief("client_proxies").getFact();
		IClient proxy = (IClient) clientProxies.get(getParameter("initiator").getValue());
		IClientService cs = (IClientService) SServiceProvider.getService(getScope().getServiceProvider(), IClientService.class).get(this);
		Set capabilities = (Set) cs.getCapabilities(proxy).get(this);
		RequestCapabilities rc = (RequestCapabilities) getParameter("action").getValue();
		rc.setCapabilities(capabilities);
		Done done = new Done();
		done.setAction(rc);
		getParameter("result").setValue(done);
	}

}
