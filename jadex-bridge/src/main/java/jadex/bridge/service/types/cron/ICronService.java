package jadex.bridge.service.types.cron;

import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Interface for adding and removing cron jobs.
 */
public interface ICronService
{
	/**
	 *  Add a schedule job.
	 *  @param job The cron job.
	 */
	@Timeout(Timeout.NONE)
	public <T> ISubscriptionIntermediateFuture<T> addJob(@CheckNotNull CronJob<T> job);
	
	/**
	 *  Remove a schedule job.
	 *  @param jobid The jobid.
	 */
	public IFuture<Void> removeJob(String jobid);
}
