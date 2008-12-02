package jadex.commons.concurrent;

import jadex.commons.SReflect;
import jadex.commons.collection.ArrayBlockingQueue;
import jadex.commons.collection.IBlockingQueue;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 *  A thread pool manages pool and saves resources
 *  and time by precreating and reusing pool.
 */
public class StrategyThreadPool implements IThreadPool
{
	//-------- constants --------
	
	/** Timeout in milliseconds before an idle thread is garbage collected. */
//	protected static final long	THREAD_TIMEOUT	= 60000;
	protected static final long	THREAD_TIMEOUT	= -1;
	
	/** The thread number. */
	protected static int threadcnt = 0;

	//-------- attributes --------

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
	public StrategyThreadPool()
	{
		this(new SimpleThreadPoolStrategy(0,10));
	}
	
	/**
	 *  Create a new thread pool.
	 */
	public StrategyThreadPool(IThreadPoolStrategy strategy)
	{
		this.strategy = strategy;
		strategy.setThreadPool(this);
		this.running = true;
		this.tasks	= new ArrayBlockingQueue();
		this.pool = new Vector();
		this.threads = new Hashtable();
	}

	//-------- methods --------

	/**
	 *  Execute a task in its own thread.
	 *  @param task The task to execute.
	 */
	public synchronized void execute(Runnable task)
	{
		//System.out.println("Execute: "+task);
		if(!running)
			throw new RuntimeException("Thread pool not running: "+this);
		
		this.strategy.taskAdded();
		this.tasks.enqueue(task);
	}

	/**
	 *  Shutdown the task pool
	 */
	public void dispose()
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

	static int cnt = 0;
	static int todo;
	/**
	 *  Main for testing.
	 *  @param args The arguments.
	 */
	public static void main(String[] args)
	{
		final StrategyThreadPool tp	= new StrategyThreadPool(new SimpleThreadPoolStrategy(0, 10));
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

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
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
	protected void addThreads(int num)
	{
		//System.out.println("Cap+1(add): "+capacity);
		for(int i=0; i<num; i++)
		{
			Thread thread = new ServiceThread();
			pool.add(thread);
			thread.start();
			System.out.println("poola: "+pool.size());
		}
	}

	/**
	 *  Get a thread for a task.
	 */
	public Thread getThread(Runnable task)
	{
		return (Thread)threads.get(task);
	}

	/**
	 *  The task for a given thread.
	 */
	public Runnable getTask(Thread thread)
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
            super("ServiceThread_"+(++threadcnt));
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
					this.task = ((Runnable)tasks.dequeue(THREAD_TIMEOUT));
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
				catch(IBlockingQueue.TimeoutException e){}
				
				if(task!=null)
				{
					threads.remove(task);
					this.task = null;
					terminate = strategy.taskFinished();
				}
			}
			
			System.out.println("poolr: "+pool.size());
			pool.remove(this);
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
}
