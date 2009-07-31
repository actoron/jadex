package jadex.bpmnbdi.task;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.concurrent.IResultListener;

/**
 *  Create a task for a space object.
 */
public class WaitForSpaceObjectTaskTask	implements ITask
{
	/**
	 *  Execute the task.
	 *  @param context	The accessible values.
	 *  @param process	The process instance executing the task.
	 *  @param listener	To be notified, when the task has completed.
	 */
	public void	execute(ITaskContext context, IProcessInstance process, IResultListener listener)
	{
		IEnvironmentSpace	space	= (IEnvironmentSpace)context.getParameterValue("space");
		Object	objectid	= context.getParameterValue("objectid");
		Object	taskid	= context.getParameterValue("taskid");
		space.addTaskListener(taskid, objectid, listener);
	}
}
