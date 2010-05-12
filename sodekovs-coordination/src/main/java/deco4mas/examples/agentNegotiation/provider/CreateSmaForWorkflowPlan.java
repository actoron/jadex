package deco4mas.examples.agentNegotiation.provider;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.javaparser.SimpleValueFetcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Creates SMAs for the workflow
 */
public class CreateSmaForWorkflowPlan extends Plan
{
	static Integer id = new Integer(0);

	public void body()
	{
		try
		{
			// get BpmnInterpreter
			IComponentManagementService cms = (IComponentManagementService) interpreter.getAgentAdapter().getServiceContainer().getService(
				IComponentManagementService.class);

			SyncResultListener lisInterpreter = new SyncResultListener();
			cms.getExternalAccess((IComponentIdentifier) ((IGoal) getReason()).getParameter("workflow").getValue(), lisInterpreter);
			BpmnInterpreter workflow = (BpmnInterpreter) lisInterpreter.waitForResult();

			Map smaMap = new HashMap();

			IComponentIdentifier[] smas = (IComponentIdentifier[]) getBeliefbase().getBeliefSet("smas").getFacts();
			if (smas != null)
			{
				for (IComponentIdentifier sma : smas)
				{
					String taskName = sma.getName().substring(4, sma.getName().indexOf("("));
					smaMap.put(taskName, sma);
					SyncResultListener lis = new SyncResultListener();
					cms.getExternalAccess(sma, lis);
					IBDIExternalAccess accSma = (IBDIExternalAccess)lis.waitForResult();
					accSma.dispatchTopLevelGoal(accSma.createGoal("assignSa"));
				}
			}

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
			// System.out.println(taskList);

			for (MActivity task : taskList)
			{

				MParameter taskParam = ((MParameter) task.getParameters().get("serviceType"));
				String taskType = (String) taskParam.getInitialValue().getValue(new SimpleValueFetcher());

				if (!smaMap.containsKey(taskType))
				{
					Map args = new HashMap();
					args.put("provider", getComponentIdentifier());
					args.put("allocatedService", taskType);

					String smaName = "Sma-" + taskType + "(" + getComponentName() + ")" + id;
					SyncResultListener lisID = new SyncResultListener();
					cms.createComponent(smaName, "deco4mas/examples/AgentNegotiation/sma/ServiceManagementAgent.agent.xml",
						new CreationInfo(null, args, this.getInterpreter().getParent().getComponentIdentifier()), lisID, null);
					IComponentIdentifier smaID = (IComponentIdentifier) lisID.waitForResult();
					getBeliefbase().getBeliefSet("smas").addFact(smaID);
					smaMap.put(taskType, smaID);
				}

			}
			id++;

			workflow.setContextVariable("smas", smaMap);

		} catch (Exception e)
		{
			System.out.println(this.getType());
			System.out.println(e.getMessage());
			fail(e);
		}
	}
}
