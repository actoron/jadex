package deco4mas.examples.agentNegotiation.sma.application.workflow.implementation;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ThreadSuspendable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceType;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

/**
 * Task executed by agent
 */
public class ExecuteServiceByAgentTask implements ITask {
	static int id = 0;

	public IFuture execute(final ITaskContext context, BpmnInterpreter instance) {
		final Future fut = new Future();
		final Logger workflowLogger = AgentLogger.getTimeEvent(instance.getComponentIdentifier().getLocalName());

		workflowLogger.info(context.getActivity().getName() + "[" + context.getParameterValue("serviceType") + "]" + " start");
		System.out.println("Bpmn task (" + context.getActivity().getName() + "/" + ((ServiceType) context.getParameterValue("serviceType")).getName() + ") start");

		IComponentManagementService cms = (IComponentManagementService) SServiceProvider.getServiceUpwards(instance.getServiceProvider(), IComponentManagementService.class).get(
				new ThreadSuspendable());

		String name = context.getActivity().getName() + "_ID" + id;
		id++;
		String model = "deco4mas/examples/agentNegotiation/sma/application/workflow/implementation/taskHandler/TaskHandler.agent.xml";

		Map args = new HashMap();

		IResultListener lis = new IResultListener() {
			public void resultAvailable(Object result) {
				workflowLogger.info(context.getActivity().getName() + "[" + context.getParameterValue("serviceType") + "]" + " end");
				try {
					fut.setResult(result);
				} catch (Exception e) {
					// omit, "may cause exeption at termination"
				}

			}

			public void exceptionOccurred(Exception exception) {
				fut.setException(exception);
			}
		};

		args.put("taskListener", lis);
		args.put("taskName", context.getParameterValue("serviceType"));
		args.put("workflow", instance.getComponentIdentifier());

		cms.createComponent(name, model, new CreationInfo(null, args, instance.getComponentIdentifier()), lis);
		return fut;
	}

	@Override
	public IFuture<Void> cancel(BpmnInterpreter instance) {
		// TODO Auto-generated method stub
		return null;
	}
}
