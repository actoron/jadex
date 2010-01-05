package jadex.bdi.wfms.clientinterface;

import java.security.AccessControlException;
import java.util.Map;

import com.sun.servicetag.UnauthorizedAccessException;

import jadex.adapter.base.fipa.Done;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.examples.cleanerworld_classic.RequestCompleteVision;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bdi.wfms.ontology.RequestProcessStart;
import jadex.bdi.wfms.ontology.RequestProxy;
import jadex.bridge.IComponentIdentifier;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IClientService;

public class RequestProcessStartPlan extends Plan
{
	public void body()
	{
		RequestProcessStart rps = (RequestProcessStart) getParameter("action").getValue();
		
		Map clientProxies = (Map) getBeliefbase().getBelief("client_proxies").getFact();
		IClient proxy = (IClient) clientProxies.get(getParameter("initiator").getValue());
		
		IClientService cs = (IClientService) getScope().getServiceContainer().getService(IClientService.class);
		
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
