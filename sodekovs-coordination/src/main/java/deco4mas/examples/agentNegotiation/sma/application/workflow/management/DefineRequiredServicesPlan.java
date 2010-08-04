package deco4mas.examples.agentNegotiation.sma.application.workflow.management;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.IFuture;
import jadex.javaparser.SimpleValueFetcher;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.common.dataObjects.RequiredService;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceType;
import deco4mas.examples.agentNegotiation.common.dataObjects.WorkflowWrapper;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

/**
 * Creates SMAs for the workflow
 */
public class DefineRequiredServicesPlan extends Plan
{
	private static Integer requestNumber = new Integer(0);

	public void body()
	{
		try
		{
			// get Logger
			Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());

			// get Cms
			IComponentManagementService cms = (IComponentManagementService) interpreter.getAgentAdapter().getServiceContainer().getService(
				IComponentManagementService.class);

			// workflow Access
			IFuture workflowFuture = cms
				.getExternalAccess((IComponentIdentifier) ((IGoal) getReason()).getParameter("workflow").getValue());
			BpmnInterpreter workflow = (BpmnInterpreter) workflowFuture.get(this);

			// LOG
			smaLogger.info("Add new needed Services for " + workflow.getComponentIdentifier().getLocalName());

			// maps of service needed to assign
			Set<RequiredService> serviceSet = new HashSet<RequiredService>();

			// get Services of workflow
			MBpmnModel model = ((BpmnInterpreter) workflow).getModelElement();
			Map allActivities = model.getAllActivities();

			List<MActivity> activities = new ArrayList<MActivity>();
			for (Iterator<MActivity> it = model.getAllActivities().values().iterator(); it.hasNext();)
			{
				activities.add(it.next());
			}

			List<MActivity> taskList = new ArrayList<MActivity>();
			for (MActivity activity : activities)
			{
				if (activity.getActivityType().equals("Task"))
					taskList.add(activity);
			}
			for (MActivity task : taskList)
			{

				MParameter taskParam = ((MParameter) task.getParameters().get("serviceType"));
				ServiceType taskType = (ServiceType) taskParam.getInitialValue().getValue(new SimpleValueFetcher());

				Boolean newService = true;
				// add all new services
				for (RequiredService nService : serviceSet)
				{
					if (nService.getServiceType().getName().equals(taskType))
					{
						newService = false;
					}
				}
				if (newService)
				{
					RequiredService needService = new RequiredService(this.getComponentIdentifier(), null, taskType);
					serviceSet.add(needService);
					smaLogger.info("Add Service " + taskType);
				}
				getBeliefbase().getBeliefSet("requiredServices").addFacts(serviceSet.toArray());

				// LOG
				((WorkflowWrapper)getBeliefbase().getBelief("workflowWrapper").getFact()).setStartTime(this.getTime());
				System.out.println();
				System.out.println("---- negotiation phase " + this.getComponentName() + " started! ----");
				System.out.println();
				smaLogger.info("--- negotiation phase ---");

				// start Negotiation
				// IGoal startNegotiation = createGoal("startNegotiation");
				// dispatchSubgoalAndWait(startNegotiation);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
