package jadex.platform.service.message.transport.udpmtp;

import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.collection.MultiCollection;

import java.util.Collection;
import java.util.Comparator;
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
	protected MultiCollection keyedtasks;
	
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
		//keyedtasks = Collections.synchronizedMap(new HashMap<Object, TimedTask>());
		keyedtasks = new MultiCollection();
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
			TimedTask task = null;
			
			synchronized(taskqueue)
			{
				task = taskqueue.peek();
				
				if (task != null)
				{
					long current = System.currentTimeMillis();
					if (current >= task.getExecutionTime())
					{
						taskqueue.poll();
						Collection<TimedTask> ktasks = (Collection<TimedTask>) keyedtasks.get(task.getKey());
						if (ktasks != null)
						{
							ktasks.remove(task);
							if (ktasks.isEmpty())
							{
								keyedtasks.remove(task.getKey());
							}
						}
						keyedtasks.remove(task.getKey());
					}
					else
					{
						try
						{
							long delta = task.getExecutionTime() - current;
							task = null;
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
			
			if (task != null)
			{
//				System.out.println("Executing: " + task);
//				task.run();
//				System.out.println("Exiting: " + task);
				threadpool.execute(task);
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
		synchronized(taskqueue)
		{
			if (task.getKey() != null)
			{
				keyedtasks.put(task.getKey(), task);
			}
//			else
//			{
//				taskqueue.remove(task);
//			}
//			if (taskqueue.size() > 10)
//			{
//				System.out.println("tasks: " + taskqueue.size() + " " + task.getClass());
//			}
			taskqueue.offer(task);
			taskqueue.notify();
		}
	}
	
	/**
	 *  Instantly executes a task.
	 *  
	 *  @param task The task.
	 */
	public void executeNow(Runnable task)
	{
		threadpool.execute(task);
	}
	
	/**
	 *  Attempts to cancel a task, there is no guarantee
	 *  that this will intercept the task in time before execution.
	 *  
	 *  @param task The task.
	 */
	public void cancel(TimedTask task)
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
	public void cancel(Object key)
	{
		Collection<TimedTask> tasks = null;
		synchronized(taskqueue)
		{
			tasks = (Collection<TimedTask>) keyedtasks.remove(key);
			if (tasks != null)
			{
				for (TimedTask task : tasks)
				{
					cancel(task);
				}
			}
		}
	}
	
	/**
	 *  Attempts to cancel a task, there is no guarantee
	 *  that this will intercept the task in time before execution.
	 *  
	 *  @param key The key identifying the task.
	 */
	public boolean hasTask(Object key)
	{
		synchronized(taskqueue)
		{
			return keyedtasks.containsKey(key);
		}
	}
	
	/**
	 *  Stops the dispatcher.
	 */
	public void stop()
	{
		synchronized (taskqueue)
		{
			running = false;
			taskqueue.notify();
		}
	}
}
