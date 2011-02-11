package deco4mas.examples.agentNegotiation.sma.application.workflow.implementation.taskHandler;

import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ExternalAccess;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.future.IFuture;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;

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

//			IFuture fut = ((IComponentManagementService)SServiceProvider.getService(
//					getScope().getServiceProvider(), IComponentManagementService.class).get(this)).getExternalAccess(aid);
						
			IComponentManagementService cms = ((IComponentManagementService)SServiceProvider.getService(
					getScope().getServiceProvider(), IComponentManagementService.class,RequiredServiceInfo.SCOPE_PLATFORM).get(this));
						
			IFuture fut = cms.getExternalAccess((IComponentIdentifier) getBeliefbase().getBelief("workflow").getFact());
			ExternalAccess exta = (ExternalAccess) fut.get(this);
			BpmnInterpreter workflow = (BpmnInterpreter) exta.getInterpreter();

			IComponentIdentifier sma = (IComponentIdentifier) workflow.getContextVariable("sma");
//			System.out.println("Task  -> " + sma.getLocalName());

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
				//Retry
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
