package org.activecomponents.platform.service.message.transport.udpmtp;

import org.activecomponents.udp.IThreadExecutor;

import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;

/**
 *  Wrapper for the daemon thread pool allowing the UDP library to access it.
 *
 */
public class DaemonThreadPoolServiceExecutor implements IThreadExecutor
{
	/** The daemon thread pool. */
	protected IDaemonThreadPoolService dtp;
	
	/**
	 *  Creates the wrapper.
	 *  
	 *  @param daemonthreadpoolservice The daemon thread pool.
	 */
	public DaemonThreadPoolServiceExecutor(IDaemonThreadPoolService daemonthreadpoolservice)
	{
		dtp = daemonthreadpoolservice;
	}
	
	/** 
	 *  Starts executing the task on a separate thread.
	 *  @param task The task.
	 */
	public void run(Runnable task)
	{
		dtp.execute(task);
	}
}
