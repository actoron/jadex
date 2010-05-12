package deco4mas.examples.agentNegotiation.provider;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.concurrent.IResultListener;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates a workflow
 */
public class PreInstantiateWorkflowPlan extends Plan
{
	static Integer id = new Integer(0);

	public void body()
	{
		final IBDIExternalAccess myself = this.getExternalAccess();
		final IComponentManagementService cms = (IComponentManagementService) interpreter.getAgentAdapter().getServiceContainer()
			.getService(IComponentManagementService.class);
		try
		{
			// create workflow
			Map args = new HashMap();
			args.put("provider", getComponentIdentifier());

			SyncResultListener lisID = new SyncResultListener();
			IResultListener killLis = new IResultListener()
			{

				@Override
				public void resultAvailable(Object source, Object result)
				{
					System.out.println("*** workflow executed *** -> RESTART!");
					IInternalEvent workflowExe = myself.createInternalEvent("workflowExecuted");
					myself.dispatchInternalEvent(workflowExe);
				}

				@Override
				public void exceptionOccurred(Object source, Exception exception)
				{
					System.out.println("Workflow not finished");
				}
			};
			String workflowName = "workflowNet" + id + "(" + getComponentName() + ")";
			String workflowType = "deco4mas/examples/agentNegotiation/provider/workflow/" + (String)getBeliefbase().getBelief("workflowName").getFact() + ".bpmn";
			cms.createComponent(workflowName, workflowType, new CreationInfo(null,
				args, this.getComponentIdentifier(), true, false), lisID, killLis);
			IComponentIdentifier workflowId = (IComponentIdentifier) lisID.waitForResult();
			getBeliefbase().getBelief("workflow").setFact(workflowId);

			// create smas
			IGoal goal = createGoal("createSmaForWorkflow");
			goal.getParameter("workflow").setValue(getBeliefbase().getBelief("workflow").getFact());
			goal.getParameter("ID").setValue(id);
			dispatchSubgoalAndWait(goal);
			id++;
			
			waitForInternalEvent("workflowExecuted");		
			IInternalEvent eva = createInternalEvent("evaluateLogger");
			dispatchInternalEvent(eva);
			
			IGoal restart = createGoal("restartWorkflow");
			dispatchTopLevelGoal(restart);
		} catch (Exception e)
		{
			System.out.println(this.getType());
			System.out.println(e.getMessage());
			fail(e);
		}
	}
}
