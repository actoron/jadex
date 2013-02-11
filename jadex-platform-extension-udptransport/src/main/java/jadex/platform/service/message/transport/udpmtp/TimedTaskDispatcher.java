package jadex.platform.service.message.transport.udpmtp;

import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 *  Dispatcher for tasks scheduled in the future.
 *
 */
public class TimedTaskDispatcher implements Runnable
{
	/** The thread pool. */
	protected IDaemonThreadPoolService threadpool;
	
	/** The task queue. */
	protected PriorityQueue<TimedTask> taskqueue;
	
	/** Tasks with a key */
	protected Map<Object, TimedTask> keyedtasks;
	
	/** Flag if the dispatcher is running */
	protected volatile boolean running;
	
	/**
	 *  Creates the dispatched
	 *  
	 *  @param threadpool The thread pool used to dispatch tasks.
	 */
	public TimedTaskDispatcher(IDaemonThreadPoolService threadpool)
	{
		this.threadpool = threadpool;
		keyedtasks = Collections.synchronizedMap(new HashMap<Object, TimedTask>());
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
		
		running = true;
		threadpool.execute(this);
	}
	
	/**
	 *  Executes the dispatcher.
	 */
	public void run()
	{
		while (running)
		{
			synchronized(taskqueue)
			{
				TimedTask task = taskqueue.peek();
				
				if (task != null)
				{
					long current = System.currentTimeMillis();
					if (current >= task.getExecutionTime())
					{
						taskqueue.poll();
						keyedtasks.remove(task.getKey());
						threadpool.execute(task);
					}
					else
					{
						try
						{
							long delta = task.getExecutionTime() - current;
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
	}
	
	/**
	 *  Schedules a task.
	 *  
	 *  @param task The task.
	 */
	public void scheduleTask(TimedTask task)
	{
		if (task.getKey() != null)
		{
			keyedtasks.put(task.getKey(), task);
		}
		
		synchronized(taskqueue)
		{
			taskqueue.offer(task);
			taskqueue.notify();
		}
	}
	
	/**
	 *  Attempts to cancel a task, there is no guarantee
	 *  that this will intercept the task in time before execution.
	 *  
	 *  @param task The task.
	 */
	public void cancelTask(TimedTask task)
	{
		synchronized (taskqueue)
		{
			taskqueue.remove(task);
		}
	}
	
	/**
	 *  Attempts to cancel a task, there is no guarantee
	 *  that this will intercept the task in time before execution.
	 *  
	 *  @param key The key identifying the task.
	 */
	public void cancelTask(Object key)
	{
		TimedTask task = keyedtasks.remove(key);
		cancelTask(task);
	}
	
	/**
	 *  Stops the dispatcher.
	 */
	public void stop()
	{
		running = false;
		taskqueue.notify();
	}
}
