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
	 */
	// Todo: how to feed in parameters?
	public void	execute(IResultListener listener);
}
