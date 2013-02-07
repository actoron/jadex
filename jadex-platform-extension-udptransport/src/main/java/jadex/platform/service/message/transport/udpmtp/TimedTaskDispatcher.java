package jadex.platform.service.message.transport.udpmtp;

import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 *  Dispatcher for tasks scheduled in the future.
 *
 */
public class TimedTaskDispatcher implements Runnable
{
	protected IDaemonThreadPoolService threadpool;
	
	/** The task queue. */
	protected PriorityQueue<TimedTask> taskqueue;
	
	/**
	 *  Creates the dispatched
	 *  
	 *  @param threadpool The thread pool used to dispatch tasks.
	 */
	public TimedTaskDispatcher(IDaemonThreadPoolService threadpool)
	{
		taskqueue = new PriorityQueue<TimedTask>(11, new Comparator<TimedTask>()
		{
			/**
			 * Compares the tasks.
			 */
			public int compare(TimedTask t1, TimedTask t2)
			{
				long diff = t1.getExecutionTime() - t2.getExecutionTime();
				int ret = diff > 0? 1 : diff < 0? -1 : 0;
				return ret;
			}
		});
	}
	
	/**
	 *  Executes the dispatcher.
	 */
	public void run()
	{
		synchronized(taskqueue)
		{
			TimedTask task = taskqueue.poll();
			
			if (task != null)
			{
				long delta = task.getExecutionTime() - System.currentTimeMillis();
				if (delta <= 0)
				{
					threadpool.execute(task);
				}
				else
				{
					try
					{
						taskqueue.wait(delta);
					}
					catch (InterruptedException e)
					{
					}
				}
			}
			else
			{
				try
				{
					taskqueue.wait();
				}
				catch (InterruptedException e)
				{
				}
			}
		}
	}
	
	public void scheduleTask(TimedTask task)
	{
		synchronized(taskqueue)
		{
			taskqueue.add(task);
			taskqueue.notify();
		}
	}
}
