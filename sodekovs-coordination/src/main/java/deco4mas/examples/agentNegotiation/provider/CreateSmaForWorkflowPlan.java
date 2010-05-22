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
import jadex.commons.IFuture;
import jadex.javaparser.SimpleValueFetcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import deco4mas.examples.agentNegotiation.ServiceType;

/**
 * Creates SMAs for the workflow
 */
public class CreateSmaForWorkflowPlan extends Plan
{
	private static Integer id = new Integer(0);

	public void body()
	{
		try
		{
			// get BpmnInterpreter
			IComponentManagementService cms = (IComponentManagementService) interpreter.getAgentAdapter().getServiceContainer().getService(
				IComponentManagementService.class);

			IFuture fut = cms.getExternalAccess((IComponentIdentifier) ((IGoal) getReason()).getParameter("workflow").getValue());
			BpmnInterpreter workflow = (BpmnInterpreter) fut.get(this);

			Map smaMap = new HashMap();

			IComponentIdentifier[] smas = (IComponentIdentifier[]) getBeliefbase().getBeliefSet("smas").getFacts();
			if (smas.length > 0)
			{
				System.out.println();
				System.out.println("---- negotiation phase " + this.getComponentName() + " started! ----");
				System.out.println();
				
				for (IComponentIdentifier sma : smas)
				{
					String taskName = sma.getName().substring(4, sma.getName().indexOf("-"));
					smaMap.put(taskName, sma);
					IFuture fut2 = cms.getExternalAccess(sma);
					waitFor(500); //HACK
					IBDIExternalAccess accSma = (IBDIExternalAccess) fut2.get(this);
					cms.suspendComponent(sma);
					accSma.dispatchTopLevelGoal(accSma.createGoal("assignSa"));
				}
				aborted();
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

					// TODO: ADD ServiceType to workflow
					// Hack!!!
					ServiceType serviceType = null;
					if (taskType.equals("Chassisbau"))
					{
						serviceType = new ServiceType("Chassisbau",1000.0,300.0,700.0,5000.0,1000.0,3000.0);
					} else if (taskType.equals("Reifenhersteller"))
					{
						serviceType = new ServiceType("Reifenhersteller",300.0,50.0,150.0,1000.0,500.0,700.0);
					} else if (taskType.equals("Endmontage"))
					{
						serviceType = new ServiceType("Endmontage",500.0,100.0,300.0,3000.0,500.0,1000.0);
					} else
					{
						System.out.println("ERROR IN CREATE-SMA-FOR-WORKFLOW: Unknown taskType " + taskType);
					}

					args.put("allocatedService", serviceType);

					String smaName = "Sma(" + taskType + "-" + getComponentName() + ")" + id;
					IFuture fut3 = cms.createComponent(smaName, "deco4mas/examples/AgentNegotiation/sma/ServiceManagementAgent.agent.xml",
						new CreationInfo(null, args, this.getInterpreter().getParent().getComponentIdentifier(),true, false), null);
					IComponentIdentifier smaID = (IComponentIdentifier) fut3.get(this);
					getBeliefbase().getBeliefSet("smas").addFact(smaID);
					smaMap.put(taskType, smaID);
					id++;
				}

			}
			workflow.setContextVariable("smas", smaMap);
			
			System.out.println();
			System.out.println("---- negotiation phase " + this.getComponentName() + " started! ----");
			System.out.println();
			
			Set<String> smaSet = smaMap.keySet();
			for (String taskName : smaSet)
			{
				cms.resumeComponent((IComponentIdentifier) smaMap.get(taskName));
			}

		} catch (Exception e)
		{
			System.out.println(this.getType());
			e.printStackTrace();
			fail(e);
		}
	}
}
