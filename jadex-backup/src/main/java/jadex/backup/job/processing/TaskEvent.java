package jadex.backup.job.processing;

import jadex.backup.job.Task;

/**
 * 
 */
public class TaskEvent extends AJobProcessingEvent
{
	/** The task. */
	protected Task task;
	
	//-------- constructors --------

	/**
	 *  Create a new job event.
	 */
	public TaskEvent()
	{
	}
	
	/**
	 *  Create a new job event.
	 */
	public TaskEvent(String type, Task task)
	{
		super(type);
		this.task = task;
	}

	//-------- methods --------

	/**
	 *  Get the task.
	 *  @return The task.
	 */
	public Task getTask()
	{
		return task;
	}

	/**
	 *  Set the task.
	 *  @param task The task to set.
	 */
	public void setTask(Task task)
	{
		this.task = task;
	}
	
	/**
	 *  Get the job id.
	 *  @return The jobid.
	 */
	public String getJobId()
	{
		return task.getId();
	}
}
