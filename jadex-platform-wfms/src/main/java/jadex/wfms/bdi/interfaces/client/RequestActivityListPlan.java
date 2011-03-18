package jadex.wfms.bdi.interfaces.client;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.Plan;
import jadex.bridge.service.SServiceProvider;
import jadex.wfms.bdi.ontology.RequestActivityList;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IClientService;

import java.security.AccessControlException;
import java.util.Map;
import java.util.Set;

public class RequestActivityListPlan extends Plan
{
	public void body()
	{
		RequestActivityList ral = (RequestActivityList) getParameter("action").getValue();
		
		Map clientProxies = (Map) getBeliefbase().getBelief("client_proxies").getFact();
		IClient proxy = (IClient) clientProxies.get(getParameter("initiator").getValue());
		
		IClientService cs = (IClientService) SServiceProvider.getService(getScope().getServiceProvider(), IClientService.class).get(this);
		
		Set activityList = null;
		try
		{
			activityList = (Set) cs.getAvailableActivities(proxy).get(this);
		}
		catch (AccessControlException e)
		{
			fail("Unauthorized Access!", e);
		}
		
		ral.setActivities(activityList);
		Done done = new Done();
		done.setAction(ral);
		getParameter("result").setValue(done);
	}
}
