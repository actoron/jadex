package deco4mas.examples.agentNegotiation.sma.workflow.implementation;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentManagementService;
import jadex.commons.concurrent.IResultListener;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

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
			final Logger workflowLogger = AgentLogger.getTimeEvent(instance.getComponentIdentifier().getLocalName());

			workflowLogger.info(context.getActivity().getName() + "[" + context.getParameterValue("serviceType") + "]" + " start");
			System.out
				.println("Bpmn task (" + context.getActivity().getName() + "/" + context.getParameterValue("serviceType") + ") start");

			IComponentManagementService cms = (IComponentManagementService) instance.getComponentAdapter().getServiceContainer()
				.getService(IComponentManagementService.class);

			String name = context.getActivity().getName() + "_ID" + id;
			id++;
			String model = "deco4mas/examples/agentNegotiation/sma/workflow/implementation/taskHandler/TaskHandler.agent.xml";

			Map args = new HashMap();

			IResultListener lis = new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					workflowLogger.info(context.getActivity().getName() + "[" + context.getParameterValue("serviceType") + "]" + " end");
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

			cms.createComponent(name, model, new CreationInfo(null, args, instance.getComponentIdentifier()), lis);

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
