package jadex.wfms.bdi.interfaces.client;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.Plan;
import jadex.wfms.bdi.ontology.PerformHeartbeat;

import java.util.Map;

public class RequestPerformHeartbeatPlan extends Plan
{
	public void body()
	{
		PerformHeartbeat phb = (PerformHeartbeat) getParameter("action").getValue();
		phb.setPerformed(true);
		
		if (!((Map) getBeliefbase().getBelief("client_proxies").getFact()).containsKey(getParameter("initiator").getValue()))
			fail();
		
		Map heartbeatTimers = (Map) getBeliefbase().getBelief("heartbeat_timers").getFact();
		heartbeatTimers.put(getParameter("initiator").getValue(), new Long(System.currentTimeMillis()));		
		
		Done done = new Done();
		done.setAction(phb);
		getParameter("result").setValue(done);
	}
}
