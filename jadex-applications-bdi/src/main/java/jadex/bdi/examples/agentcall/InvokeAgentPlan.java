package jadex.bdi.examples.agentcall;

import jadex.bdi.runtime.Plan;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.IFuture;

/**
 * 
 */
public class InvokeAgentPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		IComponentManagementService cms = (IComponentManagementService)getScope().getServiceContainer().getService(IComponentManagementService.class);
		IFuture result = cms.createComponent("a", "jadex/bdi/examples/agentcall/A.agent.xml", new CreationInfo("no_plan", null), null);
		IComponentIdentifier cid = (IComponentIdentifier)result.get(this);
		System.out.println("started agent: "+cid);
	}
}
