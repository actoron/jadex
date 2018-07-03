package org.activecomponents.udp;

/**
 *  Interface for executing tasks on parallel threads.
 *
 */
public interface IThreadExecutor
{
	/** 
	 *  Starts executing the task on a separate thread.
	 *  @param task The task.
	 */
	public void run(Runnable task);
}
