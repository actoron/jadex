package deco4mas.examples.agentNegotiation.provider.workflow.taskHandler;

import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.IFuture;
import jadex.commons.concurrent.IResultListener;
import java.util.Map;

/**
 * Execute a service
 */
public class ExecuteTaskPlan extends Plan
{
	public void body()
	{
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
				serviceAllocate.getParameter("action").setValue(taskName);
				serviceAllocate.getParameter("receiver").setValue(sma);
				dispatchSubgoalAndWait(serviceAllocate);
				Boolean result = (Boolean) serviceAllocate.getParameter("result").getValue();
				if (result != null)
					success = result;
			} catch (GoalFailureException gfe)
			{
				success = false;
			}
			if (!success)
			{
				System.out.println("*** TASK FAIL! ***");
			}
//			waitForCondition("taskListenerPresent");
//			
//			IResultListener lis = ((IResultListener) getBeliefbase().getBelief("taskListener").getFact());
//			lis.resultAvailable(this, success);
			killAgent();
		} catch (Exception e)
		{
			System.out.println(this.getType());
			System.out.println(e.getMessage());
			fail(e);
		}

	}
}
