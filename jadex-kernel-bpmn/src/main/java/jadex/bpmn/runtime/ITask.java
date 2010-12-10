package jadex.bpmn.runtime;

import jadex.commons.IFuture;

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
	 *  @return	To be notified, when the task has completed.
	 */
	public IFuture execute(ITaskContext context, BpmnInterpreter process);
	
	// Todo: Provide cancel() method for tasks no longer required
	// (e.g. when subprocess finished while task not completed)
	// to allow tasks doing some cleanup.
	// public void	cancel();
}
