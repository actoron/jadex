package org.activecomponents.udp;

/**
 *  Executor based on daemon threads.
 *
 */
public class DaemonThreadExecutor implements IThreadExecutor
{
	/** 
	 *  Starts executing the task on a separate thread.
	 *  @param task The task.
	 */
	public void run(Runnable task)
	{
		Thread t = new Thread(task);
		t.setDaemon(true);
		t.start();
	}
}
