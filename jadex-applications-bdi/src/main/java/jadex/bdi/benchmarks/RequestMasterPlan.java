package jadex.bdi.benchmarks;

import jadex.bdi.runtime.Plan;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentManagementService;

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
		args.put("receiver", getComponentIdentifier());
		
		
		IComponentManagementService	ces	= (IComponentManagementService)getScope().getRequiredService("cms").get(this);
		ces.createComponent(null, "jadex/bdi/benchmarks/RequestPerformance.agent.xml", new CreationInfo("default", args, getComponentIdentifier()), null);
	}	
}
