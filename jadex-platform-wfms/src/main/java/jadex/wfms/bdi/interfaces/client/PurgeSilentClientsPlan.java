package jadex.wfms.bdi.interfaces.client;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bdi.runtime.Plan;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IClientService;

public class PurgeSilentClientsPlan extends Plan
{
	
	public void body()
	{
		long time = System.currentTimeMillis();
		
		Map heartbeatTimers = (Map) getBeliefbase().getBelief("heartbeat_timers").getFact();
		Map clientProxies = (Map) getBeliefbase().getBelief("client_proxies").getFact();
		IClientService cs = (IClientService) getScope().getServiceProvider().getService(IClientService.class);
		
		long clientTimeout = ((Long) getBeliefbase().getBelief("client_timeout").getFact()).longValue();
		
		List purgeCandidates = new LinkedList();
		
		for (Iterator it = heartbeatTimers.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) it.next();
			if ((time - ((Long) entry.getValue()).longValue()) > clientTimeout)
			{
				purgeCandidates.add(entry.getKey());
			}
		}
		
		for (Iterator it = purgeCandidates.iterator(); it.hasNext(); )
		{
			Object cand = it.next();
			System.out.println("Purging: " + String.valueOf(cand));
			heartbeatTimers.remove(cand);
			IClient proxy = (IClient) clientProxies.remove(cand);
			System.out.println(proxy);
			cs.deauthenticate(proxy);
		}
	}
}
