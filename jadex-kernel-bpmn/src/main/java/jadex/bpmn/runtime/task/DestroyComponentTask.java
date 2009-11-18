package jadex.bpmn.runtime.task;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentIdentifier;

/**
 *  Task for destroying a component.
 */
public class DestroyComponentTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, BpmnInterpreter instance)
	{
		IComponentIdentifier cid = (IComponentIdentifier)context.getParameterValue("componentid");
		
		IComponentExecutionService ces = (IComponentExecutionService)instance.getComponentAdapter().getServiceContainer().getService(IComponentExecutionService.class);
		ces.destroyComponent(cid, null);
	}
}
