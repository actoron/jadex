package jadex.backup.job;

import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

/**
 * 
 */
public interface IJobService
{
	/**
	 *  Add a new job.
	 *  @param job The job.
	 */
	public IFuture<Void> addJob(Job job);
	
	/**
	 *  Remove a job.
	 *  @param jobid The job id.
	 */
	public IFuture<Void> removeJob(String jobid);
	
	/**
	 *  Get all jobs. 
	 *  @return All jobs.
	 */
	public IIntermediateFuture<Job> getJobs();
}
