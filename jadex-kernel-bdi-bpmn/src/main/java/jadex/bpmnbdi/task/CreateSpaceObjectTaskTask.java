package jadex.bpmnbdi.task;

import java.util.Map;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;

/**
 *  Create a task for a space object.
 */
public class CreateSpaceObjectTaskTask	extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, IProcessInstance instance)
	{
		String type	= (String)context.getParameterValue("type");
		IEnvironmentSpace	space	= (IEnvironmentSpace)context.getParameterValue("space");
		Object	objectid	= context.getParameterValue("objectid");
		Map	properties	= context.hasParameterValue("properties")
			? (Map)context.getParameterValue("properties") : null;
		
		Object	taskid	= space.createObjectTask(type, properties, objectid);
		
		if(context.hasParameterValue("taskid"))
			context.setParameterValue("taskid", taskid);
		
	}
}
