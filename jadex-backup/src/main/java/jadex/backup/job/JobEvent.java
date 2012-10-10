package jadex.backup.job;

/**
 *  Event that is used to signal job changes.
 */
public class JobEvent
{
	//-------- constants --------
	
	/** Constant for indicating when a job was added. */
	public static final String JOB_ADDED = "job_added";
	
	/** Constant for indicating when a job was removed. */
	public static final String JOB_REMOVED = "job_removed";
	
	/** Constant for indicating when a job was changed. */
	public static final String JOB_CHANGED = "job_changed";
	
	//-------- attributes --------

	/** The event type. */
	protected String type;
	
	/** The job. */
	protected Job job;
	
	//-------- constructors --------

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
	
}
