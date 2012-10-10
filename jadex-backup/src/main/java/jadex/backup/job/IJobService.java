package jadex.backup.job;

import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Job management interface. Can be used to add/remove jobs
 *  and get current jobs.
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
	 *  Modify a job.
	 *  @param job The job.
	 */
	public IFuture<Void> modifyJob(Job job);
	
	/**
	 *  Get all jobs. 
	 *  @return All jobs.
	 */
	public IIntermediateFuture<Job> getJobs();
	
	/**
	 *  Subscribe for job news.
	 */
	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<JobEvent> subscribe();
}
