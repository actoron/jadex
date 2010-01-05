package jadex.bdi.wfms.clientinterface;

import jadex.adapter.base.fipa.Done;
import jadex.bdi.runtime.Plan;
import jadex.bdi.wfms.ontology.RequestProcessStart;
import jadex.bdi.wfms.ontology.RequestWorkitemList;
import jadex.wfms.client.IClient;
import jadex.wfms.client.Workitem;
import jadex.wfms.service.IClientService;

import java.security.AccessControlException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class RequestWorkitemListPlan extends Plan
{
	public void body()
	{
		RequestWorkitemList rwl = (RequestWorkitemList) getParameter("action").getValue();
		
		Map clientProxies = (Map) getBeliefbase().getBelief("client_proxies").getFact();
		IClient proxy = (IClient) clientProxies.get(getParameter("initiator").getValue());
		
		IClientService cs = (IClientService) getScope().getServiceContainer().getService(IClientService.class);
		
		Set workitemList = null;
		try
		{
			workitemList = cs.getAvailableWorkitems(proxy);
		}
		catch (AccessControlException e)
		{
			fail("Unauthorized Access!", e);
		}
		
		rwl.setWorkitems(workitemList);
		Done done = new Done();
		done.setAction(rwl);
		getParameter("result").setValue(done);
	}
}
