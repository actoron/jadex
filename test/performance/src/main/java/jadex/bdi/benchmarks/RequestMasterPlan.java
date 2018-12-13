package jadex.bdi.benchmarks;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.service.types.cms.CreationInfo;

/**
 *	Start the partner agent.
 */
public class RequestMasterPlan extends Plan
{
	public void body()
	{
		Map<String, Object>	args	= new HashMap<String, Object>();
		args.put("master", Boolean.FALSE);
		args.put("max", getBeliefbase().getBelief("max").getFact());
		args.put("receiver", getComponentIdentifier());
		
		getAgent().createComponent(new CreationInfo("default", args).setFilename("jadex/bdi/benchmarks/RequestPerformance.agent.xml"));
	}	
}
