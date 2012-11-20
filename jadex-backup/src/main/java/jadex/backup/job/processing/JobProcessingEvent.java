package jadex.backup.job.processing;

import jadex.backup.job.Job;

/**
 * 
 */
public class JobProcessingEvent extends AJobProcessingEvent
{
	//-------- attributes --------

	/** The job. */
	protected Job job;
	
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
		super(type);
		this.job = job;
	}
	
	//-------- methods --------

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
	 *  Get the job id.
	 *  @return The jobid.
	 */
	public String getJobId()
	{
		return job.getId();
	}
}
