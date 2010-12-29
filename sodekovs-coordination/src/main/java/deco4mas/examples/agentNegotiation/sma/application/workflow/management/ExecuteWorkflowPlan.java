package deco4mas.examples.agentNegotiation.sma.application.workflow.management;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.IFuture;
import jadex.commons.ThreadSuspendable;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.SServiceProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import deco4mas.examples.agentNegotiation.common.dataObjects.WorkflowData;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ValueLogger;

/**
 * Creates a workflow and init the necessary negotiations
 */
public class ExecuteWorkflowPlan extends Plan
{
	/* id of the workflow */
	private static Integer workflowID = new Integer(0);

	public void body()
	{
		try
		{
			// name of new workflow
			final String workflowName = "workflowNet" + workflowID + "(" + getComponentName() + ")";

			// get Loggers
			final Logger workflowLogger = AgentLogger.getTimeEvent(workflowName);
			final Logger providerLogger = AgentLogger.getTimeEvent(this.getComponentName());
			final Logger workflowTimeLogger = AgentLogger.getTimeEvent("workflowTimes (" + this.getComponentName() + ")");

			// create workflow
			Map workflowArgs = new HashMap();
			workflowArgs.put("sma", getComponentIdentifier());
			
			

			// listener at component kill / workflow execution
			final IBDIExternalAccess myExternalAccess = this.getExternalAccess();
			IResultListener killListener = new IResultListener()
			{
				/** called when workflow is executed */
				@Override
				public void resultAvailable(Object result)
				{
					System.out.println("*** workflow executed ***");

					// workflow executed -> init InternalEvent
					try
					{
						myExternalAccess.scheduleStep(new IComponentStep() {
							
							@Override
							public Object execute(IInternalAccess ia) {
								IBDIInternalAccess bia = (IBDIInternalAccess)ia;
								IInternalEvent workflowExecuted = bia.getEventbase().createInternalEvent("workflowExecuted");
								bia.getEventbase().dispatchInternalEvent(workflowExecuted);
								return null;
							}
						});
						
					} catch (Exception e)
					{
						//omit
						//exception, when terminated
					}
					
				}

				/** called when workflow throw exception */
				@Override
				public void exceptionOccurred(Exception exception)
				{
					// normally not called / just log
					workflowLogger.severe("workflow not finished");
					providerLogger.severe("workflow not finished");
					workflowTimeLogger.severe("workflow not finished");
					System.out.println("workflow not finished");
				}
			};
			// now create workflow component
			String workflowType = "deco4mas/examples/agentNegotiation/sma/application/workflow/implementation/"
				+ (String) getBeliefbase().getBelief("workflowName").getFact() + ".bpmn";
			
			IComponentManagementService cms = (IComponentManagementService)SServiceProvider.getServiceUpwards(
					interpreter.getServiceProvider(), IComponentManagementService.class).get(new ThreadSuspendable());
			
			IFuture workflowFuture = cms.createComponent(workflowName, workflowType, new CreationInfo(null, workflowArgs, this
					.getComponentIdentifier(), true, false), killListener);
						
//			IFuture workflowFuture = ((IComponentManagementService) interpreter.getAgentAdapter().getServiceContainer().getService(
//					IComponentManagementService.class)).createComponent(workflowName, workflowType, new CreationInfo(null, workflowArgs, this
//					.getComponentIdentifier(), true, false), killListener);
			
			IComponentIdentifier workflowIdentifier = (IComponentIdentifier) workflowFuture.get(this);
			getBeliefbase().getBelief("workflow").setFact(workflowIdentifier);
			getBeliefbase().getBelief("workflowData").setFact(
				new WorkflowData(workflowIdentifier, (Long) getBeliefbase().getBelief("workflowIntendedTime").getFact(),
					(Double) getBeliefbase().getBelief("workflowProfit").getFact(), (Double) getBeliefbase().getBelief("negotiationCosts")
						.getFact()));
			// LOG
			providerLogger.info("workflow [" + workflowName + "] created");
			workflowLogger.info("workflow created");

			// create smas
			IGoal goal = createGoal("defineServices");
			goal.getParameter("workflow").setValue(workflowIdentifier);
			dispatchSubgoal(goal);
			workflowID++;

			waitForInternalEvent("workflowExecuted");
			
			// LOG
			workflowLogger.info("workflow executed");
			providerLogger.info("*** workflow executed ***");
			WorkflowData data = (WorkflowData) getBeliefbase().getBelief("workflowData").getFact();
			data.setEndTime(getTime());
			workflowTimeLogger.info("Execution time: " + data.toString());
			ValueLogger.addValue("workflowTime", data.getCompleteTime().doubleValue());
			workflowTimeLogger.info("Complete time: " + data.getExecutionTime().toString());
			workflowTimeLogger.info("---");
			
			dispatchSubgoalAndWait(createGoal("evaluateWorkflow"));

			IGoal restartWorkflow = createGoal("restartWorkflow");
			dispatchTopLevelGoal(restartWorkflow);
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
