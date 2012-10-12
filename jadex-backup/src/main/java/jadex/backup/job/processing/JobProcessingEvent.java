package jadex.backup.job.processing;

import jadex.backup.job.Job;
import jadex.backup.job.Task;

/**
 * 
 */
public class JobProcessingEvent
{
	/** Task was added. */
	public static final String INITIAL = "initial";
	
	/** Task was added. */
	public static final String TASK_ADDED = "task_added";
	
	/** Task was removed. */
	public static final String TASK_REMOVED = "task_removed";

	/** Task has changed. */
	public static final String TASK_CHANGED = "task_changed";

	//-------- attributes --------

	/** The event type. */
	protected String type;
	
	/** The job. */
	protected Job job;
	
	/** The job id. */
	protected String jobid;
	
	/** The task. */
	protected Task task;
	
	//-------- constructors --------

	/**
	 *  Create a new job event.
	 */
	public JobProcessingEvent()
	{
	}
	
	/**
	 *  Create a new job event.
	 */
	public JobProcessingEvent(String type, Job job)
	{
		this(type, job, null);
	}
	
	/**
	 *  Create a new job event.
	 */
	public JobProcessingEvent(String type, Job job, Task task)
	{
		this.type = type;
		this.job = job;
		this.task = task;
	}
	
	/**
	 *  Create a new job event.
	 */
	public JobProcessingEvent(String type, String jobid, Task task)
	{
		this.type = type;
		this.jobid = jobid;
		this.task = task;
	}

	//-------- methods --------

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 *  Get the job.
	 *  @return The job.
	 */
	public Job getJob()
	{
		return job;
	}

	/**
	 *  Set the job.
	 *  @param job The job to set.
	 */
	public void setJob(Job job)
	{
		this.job = job;
	}

	/**
	 *  Get the jobid.
	 *  @return The jobid.
	 */
	public String getJobId()
	{
		return jobid;
	}

	/**
	 *  Set the jobid.
	 *  @param jobid The jobid to set.
	 */
	public void setJobId(String jobid)
	{
		this.jobid = jobid;
	}

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
	
}
