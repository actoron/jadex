package deco4mas.examples.agentNegotiation.provider;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;

/**
 * starts the workflow
 */
public class StartWorkflowPlan extends Plan
{
	public void body()
	{
		try
		{	
			IComponentManagementService cms = (IComponentManagementService) interpreter.getAgentAdapter().getServiceContainer().getService(
				IComponentManagementService.class);

			cms.resumeComponent((IComponentIdentifier) getBeliefbase().getBelief("workflow").getFact(), null);
		} catch (Exception e)
		{
			System.out.println(this.getType());
			fail(e);
		}
		
	}
}
