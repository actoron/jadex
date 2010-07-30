package deco4mas.examples.agentNegotiation.sma.application.workflow.implementation.taskHandler;

import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.IFuture;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceType;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

/**
 * Execute a service
 */
public class ExecuteTaskPlan extends Plan
{
	public void body()
	{
		try
		{
			Logger workflowLogger = AgentLogger.getTimeEvent(((IComponentIdentifier) getBeliefbase().getBelief("workflow").getFact())
				.getLocalName());

			// System.out.println("task(handler) " + taskName + ": start");

			IComponentManagementService cms = (IComponentManagementService) interpreter.getAgentAdapter().getServiceContainer().getService(
				IComponentManagementService.class);
			IFuture fut = cms.getExternalAccess((IComponentIdentifier) getBeliefbase().getBelief("workflow").getFact());
			BpmnInterpreter workflow = (BpmnInterpreter) fut.get(this);

			IComponentIdentifier sma = (IComponentIdentifier) workflow.getContextVariable("sma");
			System.out.println("Task  -> " + sma.getLocalName());

			// ask sma for allocate
			Boolean success = false;

			String taskName = (String) ((ServiceType) getBeliefbase().getBelief("taskName").getFact()).getName();
			try
			{
				IGoal serviceAllocate = createGoal("rp_initiate");
				serviceAllocate.getParameter("action").setValue(taskName);
				serviceAllocate.getParameter("receiver").setValue(sma);
				dispatchSubgoalAndWait(serviceAllocate);
				Boolean result = (Boolean) serviceAllocate.getParameter("result").getValue();
				if (result != null)
					success = result;
			} catch (GoalFailureException gfe)
			{
				// gfe.printStackTrace();
				success = false;
			}
			if (!success)
			{
				// workflowLogger.warning("TASK " + taskName + " FAIL!");
				// System.out.println("TASK " + taskName + " FAIL!");
				body();
			} else
			{
				killAgent();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}

	}
}
