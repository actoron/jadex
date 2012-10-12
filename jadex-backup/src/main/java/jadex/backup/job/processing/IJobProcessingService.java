package jadex.backup.job.processing;

import jadex.backup.job.Job;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.IFilter;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Offered by a job agent to provide continuous feedback regarding 
 *  the processing of a specific job. 
 */
public interface IJobProcessingService
{
	/**
	 *  Get the job of this service.
	 */
	public IFuture<Job> getJob();
	
	/**
	 *  Modify a job.
	 *  @param job The job.
	 */
	public IFuture<Void> modifyJob(Job job);
	
	/**
	 * 
	 */
	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<JobProcessingEvent> subscribe(IFilter<JobProcessingEvent> filter);
}
