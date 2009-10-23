package jadex.bdi.benchmarks;

import jadex.adapter.base.fipa.IAMS;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;

import java.util.HashMap;
import java.util.Map;

/**
 *	Start the partner agent.
 */
public class RequestMasterPlan extends Plan
{
	public void body()
	{
		Map	args	= new HashMap();
		args.put("master", Boolean.FALSE);
		args.put("max", getBeliefbase().getBelief("max").getFact());
		args.put("receiver", getAgentIdentifier());
		
		IAMS	ams	= (IAMS) getScope().getPlatform().getService(IAMS.class);
		SyncResultListener	srl	= new SyncResultListener();
		ams.createAgent(null, "jadex/bdi/benchmarks/RequestPerformance.agent.xml", "default", args, srl, getAgentIdentifier());
		IComponentIdentifier	slave	= (IComponentIdentifier)srl.waitForResult();
		ams.startAgent(slave, null);
	}	
}
