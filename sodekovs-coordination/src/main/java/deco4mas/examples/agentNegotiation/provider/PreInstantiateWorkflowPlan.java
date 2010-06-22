package deco4mas.examples.agentNegotiation.provider;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.IFuture;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ValueLogger;

/**
 * Creates a workflow
 */
public class PreInstantiateWorkflowPlan extends Plan
{
	private static Integer id = new Integer(0);

	public void body()
	{
		IServiceContainer container = interpreter.getAgentAdapter().getServiceContainer();

		final String workflowName = "workflowNet" + id + "(" + getComponentName() + ")";
		final Logger workflowLogger = AgentLogger.getTimeEvent(workflowName);
		final Logger providerLogger = AgentLogger.getTimeEvent(this.getComponentName());
		final Logger workflowTime = AgentLogger.getTimeEvent("workflowTimes (" + this.getComponentName() + ")");

		final IBDIExternalAccess myself = this.getExternalAccess();
		IComponentManagementService cms = (IComponentManagementService) container.getService(IComponentManagementService.class);
		try
		{
			Map args = new HashMap();
			args.put("provider", getComponentIdentifier());

			IResultListener killLis = new IResultListener()
			{

				@Override
				public void resultAvailable(Object source, Object result)
				{
					// LOG
					workflowLogger.info("workflow executed");
					providerLogger.info("*** workflow executed ***");

					Long executionTime = getTime() - (Long) getBeliefbase().getBelief("statExetime").getFact();
					Long allTime = getTime() - (Long) getBeliefbase().getBelief("statNegtime").getFact();
					workflowTime.info("Execution time: " + executionTime);
					ValueLogger.addValue("workflowTime", allTime.doubleValue());
					workflowTime.info("Complete time: " + allTime);
					workflowTime.info("---");

					System.out.println("*** workflow executed ***");
					IInternalEvent workflowExe = myself.createInternalEvent("workflowExecuted");
					myself.dispatchInternalEvent(workflowExe);
				}

				@Override
				public void exceptionOccurred(Object source, Exception exception)
				{
					workflowLogger.severe("workflow not finished");
					providerLogger.severe("workflow not finished");
					System.out.println("workflow not finished");
				}
			};
			String workflowType = "deco4mas/examples/agentNegotiation/provider/workflow/"
				+ (String) getBeliefbase().getBelief("workflowName").getFact() + ".bpmn";
			IFuture fut = cms.createComponent(workflowName, workflowType, new CreationInfo(null, args, this.getComponentIdentifier(), true,
				false), killLis);
			// waitFor(1000); //HACK!!!
			IComponentIdentifier workflowId = (IComponentIdentifier) fut.get(this);
			getBeliefbase().getBelief("workflow").setFact(workflowId);

			// LOG
			providerLogger.info("workflow [" + workflowName + "] created");
			workflowLogger.info("workflow created");

			// create smas
			IGoal goal = createGoal("createSmaForWorkflow");
			goal.getParameter("workflow").setValue(getBeliefbase().getBelief("workflow").getFact());
			goal.getParameter("ID").setValue(id);
			dispatchSubgoalAndWait(goal);
			id++;

			waitForInternalEvent("workflowExecuted");
			IGoal restart = createGoal("restartWorkflow");
			dispatchTopLevelGoal(restart);
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
