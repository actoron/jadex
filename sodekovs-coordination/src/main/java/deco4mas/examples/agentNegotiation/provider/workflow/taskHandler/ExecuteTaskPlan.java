package deco4mas.examples.agentNegotiation.provider.workflow.taskHandler;

import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.IFuture;
import java.util.Map;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

/**
 * Execute a service
 */
public class ExecuteTaskPlan extends Plan
{
	public void body()
	{
		Logger workflowLogger = AgentLogger.getTimeEvent(((IComponentIdentifier) getBeliefbase().getBelief("workflow").getFact())
			.getLocalName());
		try
		{
			String taskName = (String) getBeliefbase().getBelief("taskName").getFact();
			// System.out.println("task(handler) " + taskName + ": start");

			IComponentManagementService cms = (IComponentManagementService) interpreter.getAgentAdapter().getServiceContainer().getService(
				IComponentManagementService.class);

			IFuture fut = cms.getExternalAccess((IComponentIdentifier) getBeliefbase().getBelief("workflow").getFact());
			BpmnInterpreter workflow = (BpmnInterpreter) fut.get(this);

			Map smaList = (Map) workflow.getContextVariable("smas");
			IComponentIdentifier sma = (IComponentIdentifier) smaList.get(taskName);

			System.out.println("Task  -> " + sma.getLocalName());

			// ask sma for allocate
			Boolean success = false;

			try
			{
				IGoal serviceAllocate = createGoal("rp_initiate");
				serviceAllocate.getParameter("action").setValue(
					((IComponentIdentifier) getBeliefbase().getBelief("workflow").getFact()).getLocalName());
				serviceAllocate.getParameter("receiver").setValue(sma);
				dispatchSubgoalAndWait(serviceAllocate);
				Boolean result = (Boolean) serviceAllocate.getParameter("result").getValue();
				if (result != null)
					success = result;
			} catch (GoalFailureException gfe)
			{
				gfe.printStackTrace();
				success = false;
			}
			if (!success)
			{
				workflowLogger.warning("TASK " + taskName + " FAIL!");
				System.out.println("TASK " + taskName + " FAIL!");
				body();
				aborted();
			}
			// waitForCondition("taskListenerPresent");
			//			
			// IResultListener lis = ((IResultListener)
			// getBeliefbase().getBelief("taskListener").getFact());
			// lis.resultAvailable(this, success);
			killAgent();
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}

	}
}
