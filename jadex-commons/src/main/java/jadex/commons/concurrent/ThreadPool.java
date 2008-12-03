package jadex.commons.concurrent;

import jadex.commons.SReflect;
import jadex.commons.collection.ArrayBlockingQueue;
import jadex.commons.collection.IBlockingQueue;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 *  A thread pool manages pool and saves resources
 *  and time by precreating and reusing pool.
 */
public class ThreadPool implements IThreadPool
{
	//-------- constants --------
	
	/** The thread number. */
	protected static int threadcnt = 0;
	
	/** The static thread pool number. */
	protected static int poolcnt = 0;

	//-------- attributes --------

	/** The thread group. */
	protected ThreadGroup group;
	
	/** The strategy. */
	protected IThreadPoolStrategy strategy;
	
	/** The pool of service threads. */
	protected List	pool;

	/** The tasks to execute. */
	// Todo: fix blocking queue.
	protected IBlockingQueue	tasks;

	/** The running flag. */
	protected boolean	running;

	/** The task - thread mapping. */
	protected Map	threads;

	//-------- constructors --------

	/**
	 *  Create a new thread pool.
	 */
	public ThreadPool()
	{
		this(new DefaultThreadPoolStrategy(0, 20, 30000));
	}
	
	/**
	 *  Create a new thread pool.
	 */
	public ThreadPool(IThreadPoolStrategy strategy)
	{
		this.strategy = strategy;
		this.group = new ThreadGroup("strategy_thread_pool_"+poolcnt++);
		this.running = true;
		this.tasks	= new ArrayBlockingQueue();
		this.pool = new ArrayList();
		this.threads = new Hashtable();
	}

	//-------- methods --------

	/**
	 *  Execute a task in its own thread.
	 *  @param task The task to execute.
	 */
	public synchronized void execute(Runnable task)
	{
		if(!running)
			throw new RuntimeException("Thread pool not running: "+this);
		
		if(this.strategy.taskAdded())
			addThreads(1);
		
		this.tasks.enqueue(task);
	}

	/**
	 *  Shutdown the task pool
	 */
	public synchronized void dispose()
	{
		this.running = false;
		this.tasks.setClosed(true);
		Thread.yield();
		for(int i=0; i<pool.size(); i++) // Hack!!! Kill all threads.
		{
			Thread t = (Thread)pool.get(i);
			t.stop();
		}
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public synchronized String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(SReflect.getInnerClassName(getClass()));
		buf.append("poolsize=");
		buf.append(pool.size());
		buf.append(", running=");
		buf.append(running);
		buf.append(")");
		return buf.toString();
	}

	//-------- helper methods --------

	/**
	 *  Create some pool.
	 *  @param num The number of pool.
	 */
	protected synchronized void addThreads(int num)
	{
		for(int i=0; i<num; i++)
		{
			Thread thread = new ServiceThread();
			// Thread gets daemon state of parent, i.e. thread daemon state would
			// depend on called thread, which is not desired.
			thread.setDaemon(false);
			pool.add(thread);
			thread.start();
//			System.out.println("poola: "+pool.size());
		}
	}

	/**
	 *  Get a thread for a task.
	 */
	protected synchronized Thread getThread(Runnable task)
	{
		return (Thread)threads.get(task);
	}

	/**
	 *  The task for a given thread.
	 */
	protected synchronized Runnable getTask(Thread thread)
	{
		Runnable	ret	= null;
		if(thread instanceof ServiceThread)
		{
			ret	= ((ServiceThread)thread).getTask();
		}
		return ret;
	}

	//-------- inner classes --------

	/**
	 *  A service thread executes tasks.
	 */
	public class ServiceThread extends Thread
	{
		//-------- attributes --------

		/** The actual task. */
		protected Runnable task;

		/** The start time. */
		protected long start;

		//-------- constructors --------

		/**
		 *  Create a new thread.
		 */
		public ServiceThread()
		{
            super(group, "ServiceThread_"+(++threadcnt));
		}

		//-------- methods --------

		/**
		 *  Dequeue an element from the queue
		 *  and execute it.
		 */
		public void run()
		{
			boolean terminate = false;
			
			while(running && !terminate)
			{
				try
				{
					this.task = ((Runnable)tasks.dequeue(strategy.getThreadTimeout()));
					threads.put(task, this);
					this.start = System.currentTimeMillis();
					this.setName(task.toString());

					try
					{
						this.task.run();
					}
					catch(ThreadDeath e){}
				}
				catch(IBlockingQueue.ClosedException e){}
				catch(IBlockingQueue.TimeoutException e)
				{
					task = null;
					terminate = strategy.threadTimeoutOccurred();
				}
				
				if(task!=null)
				{
					threads.remove(task);
					this.task = null;
					terminate = strategy.taskFinished();
				}
			}
			
			synchronized(ThreadPool.this)
			{
				pool.remove(this);
			}
		}

		/**
		 *  Get the runnable (the task).
		 *  @return The runnable.
		 */
		public Runnable getTask()
		{
			return this.task;
		}
	}
	
	//-------- static part --------
	
	static int cnt = 0;
	static int todo;
	/**
	 *  Main for testing.
	 *  @param args The arguments.
	 */
	public static void main(String[] args)
	{
		final ThreadPool tp	= new ThreadPool(new DefaultThreadPoolStrategy(0, 10, 10000));
		int max = 10000;
		todo = max;
		for(int i=0; i<max; i++)
		{
			tp.execute(new Runnable()
			{
				int n = cnt++;
				public void run()
				{
					String t = Thread.currentThread().toString();
					System.out.println("a_"+this+" : "+t);
					try{Thread.sleep(100);}
					catch(InterruptedException e){}
					System.out.println("b_"+this+" : "+t);
					synchronized(tp)
					{
						todo--;
						if(todo==0)
							System.out.println("Execution finished.");
					}
				}

				public String toString()
				{
					return "Task_"+n;
				}
			});
		}
	}
}
