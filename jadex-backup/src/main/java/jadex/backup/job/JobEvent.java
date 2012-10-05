package jadex.backup.job;

/**
 * 
 */
public class JobEvent
{
	public static final String JOB_ADDED = "job_added";
	public static final String JOB_REMOVED = "job_removed";
	public static final String JOB_CHANGED = "job_changed";
	
	/** The event type. */
	protected String type;
	
	/** The job. */
	protected Job job;
	
	/**
	 *  Create a new job event.
	 */
	public JobEvent()
	{
	}
	
	/**
	 *  Create a new job event.
	 */
	public JobEvent(String type, Job job)
	{
		this.type = type;
		this.job = job;
	}

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
	
}
