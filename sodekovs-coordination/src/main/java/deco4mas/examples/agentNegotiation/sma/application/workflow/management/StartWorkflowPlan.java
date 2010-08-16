package deco4mas.examples.agentNegotiation.sma.application.workflow.management;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.common.dataObjects.WorkflowData;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

/**
 * starts the workflow
 */
public class StartWorkflowPlan extends Plan
{
	public void body()
	{
		try
		{
			// get Logger
			final Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());
			final Logger workflowTime = AgentLogger.getTimeEvent("workflowTimes (" + this.getComponentName() + ")");

			// LOG
			System.out.println();
			System.out.println("---- Execution phase " + this.getComponentName() + " started! ----");
			System.out.println();
			smaLogger.info("--- Execution phase ---");
			getBeliefbase().getBelief("executionPhase").setFact(new Boolean(true));
			((WorkflowData)getBeliefbase().getBelief("workflowData").getFact()).setStartExecutionTime(this.getTime());
			Long negTime = getTime() - ((WorkflowData)getBeliefbase().getBelief("workflowData").getFact()).getStartTime().longValue();;
			workflowTime.info("Negotiation time: " + negTime);

			// start workflow
			IComponentManagementService cms = (IComponentManagementService) interpreter.getAgentAdapter().getServiceContainer().getService(
				IComponentManagementService.class);
			cms.resumeComponent((IComponentIdentifier) getBeliefbase().getBelief("workflow").getFact());
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
