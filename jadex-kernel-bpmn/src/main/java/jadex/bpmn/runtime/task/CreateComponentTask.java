package jadex.bpmn.runtime.task;

import java.util.Map;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bridge.IComponentExecutionService;

/**
 *  Task for creating a component.
 */
public class CreateComponentTask extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, BpmnInterpreter instance)
	{
		String name = (String)context.getParameterValue("name");
		String model = (String)context.getParameterValue("model");
		String config = (String)context.getParameterValue("configuration");
		Map args = (Map)context.getParameterValue("arguments");
		boolean suspend = context.getParameterValue("suspend")!=null? ((Boolean)context.getParameterValue("suspend")).booleanValue(): false;
		Object creator = context.getParameterValue("creator");
		
		IComponentExecutionService ces = (IComponentExecutionService)instance.getComponentAdapter().getServiceContainer().getService(IComponentExecutionService.class);
		ces.createComponent(name, model, config, args, suspend, null, creator);
	}
}
