package jadex.bridge.service.types.cron;

import jadex.commons.future.IFuture;

/**
 *  Interface for adding and removing cron jobs.
 */
public interface ICronService 
{
	/**
	 *  Add a schedule job.
	 *  @param job The cron job.
	 */
	public IFuture<Void> addJob(CronJob job);
	
	/**
	 *  Remove a schedule job.
	 *  @param jobid The jobid.
	 */
	public IFuture<Void> removeJob(String jobid);
}
