package jadex.backup.job.processing;

import jadex.backup.job.Job;
import jadex.backup.job.Task;
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
	 *  Get the job id.
	 *  
	 *  The id is static, i.e. it does not change during
	 *  the lifetime of the job agent.
	 */
	public String	getJobId();

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
	 *  Modify a task.
	 *  @param task The task.
	 */
	public IFuture<Void> modifyTask(Task task);
	
	/**
	 * 
	 */
	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<AJobProcessingEvent> subscribe(IFilter<AJobProcessingEvent> filter);
}
