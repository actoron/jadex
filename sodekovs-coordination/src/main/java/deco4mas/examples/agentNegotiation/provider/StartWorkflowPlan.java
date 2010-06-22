package deco4mas.examples.agentNegotiation.provider;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

/**
 * starts the workflow
 */
public class StartWorkflowPlan extends Plan
{
	public void body()
	{
		final Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());
		final Logger workflowTime = AgentLogger.getTimeEvent("workflowTimes (" + this.getComponentName() + ")");

		try
		{
			System.out.println();
			System.out.println("---- Execution phase" + this.getComponentName() + "started! ----");
			System.out.println();
			smaLogger.info("--- Execution phase ---");

			IComponentManagementService cms = (IComponentManagementService) interpreter.getAgentAdapter().getServiceContainer().getService(
				IComponentManagementService.class);

			getBeliefbase().getBelief("statExetime").setFact(this.getTime());
			Long negTime = getTime() - (Long) getBeliefbase().getBelief("statNegtime").getFact();
			workflowTime.info("Negotiation time: " + negTime);

			cms.resumeComponent((IComponentIdentifier) getBeliefbase().getBelief("workflow").getFact());
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}

	}
}
