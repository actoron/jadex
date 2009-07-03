package jadex.bpmn.runtime;

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
	public void	execute();
}
