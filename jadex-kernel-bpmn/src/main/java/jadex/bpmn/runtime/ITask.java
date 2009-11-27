package jadex.bpmn.runtime;

import jadex.commons.concurrent.IResultListener;

/**
 *  Interface for domain specific tasks.
 *  The implementation of a task is annotated in BPMN using the 'class' property.
 */
public interface ITask
{
	/**
	 *  Execute the task.
	 *  @param context	The accessible values.
	 *  @param process	The process instance executing the task.
	 *  @param listener	To be notified, when the task has completed.
	 */
	public void	execute(ITaskContext context, BpmnInterpreter process, IResultListener listener);
}
