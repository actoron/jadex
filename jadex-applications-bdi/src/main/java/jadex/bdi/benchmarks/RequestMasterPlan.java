package jadex.bdi.benchmarks;

import jadex.bdi.runtime.Plan;
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
		
		IComponentManagementService	ces	= (IComponentManagementService)getScope().getServiceContainer().getService(IComponentManagementService.class);
		ces.createComponent(null, "jadex/bdi/benchmarks/RequestPerformance.agent.xml", "default", args, false, null, getComponentIdentifier(), null, false);
	}	
}
