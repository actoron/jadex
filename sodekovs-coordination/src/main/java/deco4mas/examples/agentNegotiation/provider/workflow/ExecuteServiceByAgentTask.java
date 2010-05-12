package deco4mas.examples.agentNegotiation.provider.workflow;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentManagementService;
import jadex.commons.concurrent.IResultListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Task executed by agent
 */
public class ExecuteServiceByAgentTask implements ITask
{
	static int id = 0;

	public void execute(final ITaskContext context, BpmnInterpreter instance, final IResultListener listener)
	{
		try
		{
			System.out.println("Bpmn task (" + context.getActivity().getName() + ") start");

			IComponentManagementService cms = (IComponentManagementService) instance.getComponentAdapter().getServiceContainer().getService(
				IComponentManagementService.class);

			String name = context.getActivity().getName() + "_ID" + id;
			id++;
			String model = "deco4mas/examples/agentNegotiation/provider/workflow/taskHandler/TaskHandler.agent.xml";

			Map args = new HashMap();

			IResultListener lis = new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					listener.resultAvailable(ExecuteServiceByAgentTask.this, result);
				}

				public void exceptionOccurred(Object source, Exception exception)
				{
					listener.exceptionOccurred(ExecuteServiceByAgentTask.this, exception);
				}
			};

			args.put("taskListener", lis);
			args.put("taskName", context.getParameterValue("serviceType"));
			args.put("workflow", instance.getComponentIdentifier());

			cms.createComponent(name, model, new CreationInfo(null, args, instance.getComponentIdentifier()), null, null);
		} catch (Exception e)
		{
			System.out.println("ExecuteServiceByAgentTask");
			System.out.println(e.getMessage());
		}
	}
}
