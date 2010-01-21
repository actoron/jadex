package jadex.wfms.bdi.interfaces.client;

import java.security.AccessControlException;
import java.util.Map;
import java.util.Set;

import jadex.adapter.base.fipa.Done;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.wfms.bdi.client.cap.AbstractWfmsPlan;
import jadex.wfms.bdi.ontology.RequestCapabilities;
import jadex.wfms.bdi.ontology.RequestModelNames;
import jadex.wfms.bdi.ontology.RequestProxy;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IClientService;
import jadex.wfms.service.IProcessDefinitionService;
import jadex.wfms.service.impl.ProcessDefinitionConnector;

public class RequestCapabilitiesPlan extends AbstractWfmsPlan
{
	public void body()
	{
		Map clientProxies = (Map) getBeliefbase().getBelief("client_proxies").getFact();
		IClient proxy = (IClient) clientProxies.get(getParameter("initiator").getValue());
		IClientService cs = ((IClientService) getScope().getServiceContainer().getService(IClientService.class));
		
		Set capabilities = cs.getCapabilities(proxy);
		
		RequestCapabilities rc = (RequestCapabilities) getParameter("action").getValue();
		rc.setCapabilities(capabilities);
		Done done = new Done();
		done.setAction(rc);
		getParameter("result").setValue(done);
	}

}
