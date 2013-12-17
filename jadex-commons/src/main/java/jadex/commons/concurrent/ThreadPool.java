package jadex.commons.concurrent;

import jadex.commons.DefaultPoolStrategy;
import jadex.commons.IPoolStrategy;
import jadex.commons.SReflect;
import jadex.commons.collection.ArrayBlockingQueue;
import jadex.commons.collection.IBlockingQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 *  A thread pool manages pool and saves resources
 *  and time by precreating and reusing pool.
 */
public class ThreadPool implements IThreadPool
{
	//-------- profiling --------
	
	/** Enable call profiling. */
	public static final boolean	PROFILING	= false;
	
	/** Print every 10 seconds. */
	public static final long	PRINT_DELAY	= 10000;
	
	/** Service calls per runnable class. */
	protected static Map<Class<?>, Integer>	calls	= PROFILING ? new HashMap<Class<?>, Integer>() : null;
	
	static
	{
		if(PROFILING)
		{
			final Timer	timer	= new Timer(true);
			final Runnable	run	= new Runnable()
			{
				public void run()
				{
					StringBuffer	out	= new StringBuffer("Threadpool executes:\n");
					synchronized(calls)
					{
						for(Class<?> c: calls.keySet())
						{
							out.append("\t").append(calls.get(c)).append(":\t")
								.append(c)
								.append("\n");
						}
					}
					System.out.println(out);
					
					final Runnable	run	= this;
					timer.schedule(new TimerTask()
					{
						public void run()
						{
							run.run();
						}
					}, PRINT_DELAY);
				}
			};
			
			timer.schedule(new TimerTask()
			{
				public void run()
				{
					run.run();
				}
			}, PRINT_DELAY);
		}
	}
	
	//-------- constants --------
	
	/** The thread number. */
	protected static int threadcnt = 0;
	
	/** The static thread pool number. */
	protected static int poolcnt = 0;

	//-------- attributes --------

	/** The thread group. */
	protected ThreadGroup group;
	
	/** The strategy. */
	protected IPoolStrategy strategy;
	
	/** The pool of service threads. */
	protected List<ServiceThread>	pool;
	
	/** The list of threads not used. */
	protected List<ServiceThread> parked;
	
	/** The tasks to execute. */
	// Todo: fix blocking queue.
	protected IBlockingQueue<Runnable>	tasks;
//	protected BlockingQueue tasks;

	/** The running flag. */
	protected volatile boolean	running;

	/** The daemon flag. */
	protected boolean daemon;
	
//	/** The task - thread mapping. */
//	protected Map	threads;
	
	/** The current throughput of the pool (average of waiting times). */
	protected Map<Runnable, Long> enqueuetimes;
	
	/** The maximum number of parked threads. */
	protected int maxparked;

	/** Rescue timer that checks if progress is made and tasks are scheduled. */
	protected Timer timer; 
	
	/** The time a task should maximum wait. */
	protected long maxwait;
	
	//-------- constructors --------

	/**
	 *  Create a new thread pool.
	 */
	public ThreadPool()
	{
		this(new DefaultPoolStrategy(0, 20, 30000, 0));
	}
	
	/**
	 *  Create a new thread pool.
	 */
	public ThreadPool(IPoolStrategy strategy)
	{
		this(false, strategy);
	}
	
	/**
	 *  Create a new thread pool.
	 */
	public ThreadPool(boolean daemon, IPoolStrategy strategy)
	{	
		this(daemon, strategy, 500);
	}
	
	/**
	 *  Create a new thread pool.
	 */
	public ThreadPool(boolean daemon, final IPoolStrategy strategy, final long maxwait)
	{		
		this.daemon = daemon;
		this.strategy = strategy;
		this.group = new ThreadGroup("strategy_thread_pool_"+poolcnt++);
		this.running = true;
		this.tasks	= new ArrayBlockingQueue<Runnable>();
//		this.tasks = new java.util.concurrent.LinkedBlockingQueue();
		this.pool = new ArrayList<ServiceThread>();
//		this.threads = new Hashtable();
		this.enqueuetimes = Collections.synchronizedMap(new IdentityHashMap<Runnable, Long>());
		this.maxparked = 500;
		this.parked = new ArrayList<ServiceThread>();
		this.maxwait = maxwait;
		
		this.timer = new Timer(true);
		timer.scheduleAtFixedRate(new TimerTask()
		{
			public void run()
			{
				synchronized(ThreadPool.this)
				{
					// If there are tasks and the last served task is too long ago
					if(running && tasks.size()>0)
					{
						Runnable	task	= tasks.peek();
						Long start = enqueuetimes.get(task);
						if(start!=null && strategy.getCapacity()==0 && start.longValue()+maxwait<System.currentTimeMillis())
						{
							addThreads(5);
							strategy.workersAdded(5);
//							System.out.println("Added threads due to starving task in queue: "+task+", "+strategy+", pool="+pool.size()+", parked="+parked.size()+", tasks="+tasks.size());
						}
					}
				}
			}
		}, maxwait, maxwait);

		
		addThreads(strategy.getWorkerCount());
	
//		System.out.println("Creating: "+this);
	}

	//-------- methods --------

	/**
	 *  Test if the thread pool is running.
	 */
	public boolean	isRunning()
	{
		return running;
	}
	
	/**
	 *  Execute a task in its own thread.
	 *  @param task The task to execute.
	 */
	public synchronized void execute(Runnable task)
	{
		// profile
		if(PROFILING)
		{
			synchronized(calls)
			{
				Integer	cnt	= calls.get(task.getClass());
				calls.put(task.getClass(), Integer.valueOf(cnt==null ? 0 : cnt.intValue()+1));
			}
		}
		
		if(!running)
		{
			throw new RuntimeException("Thread pool not running: "+this);
		}
		
		if(this.strategy.taskAdded())
		{
			addThreads(1);
		}
		
		if(enqueuetimes.put(task, System.currentTimeMillis()) != null)
		{
			throw new RuntimeException("Task already scheduled: " + task);
		}
		
		tasks.enqueue(task);
	}

	/**
	 *  Shutdown the task pool
	 */
	public synchronized void dispose()
	{
//		System.out.println("dispose: "+this+", "+pool.size()+", "+parked.size());
		this.running = false;
		this.tasks.setClosed(true);

		if(timer!=null)
		{
			this.timer.cancel();
		}
		
		while(!pool.isEmpty())
		{
			ServiceThread thread = (ServiceThread)pool.remove(0);
			synchronized(thread)
			{
				thread.terminated = true;
				thread.notify();
			}
		}
		
		while(!parked.isEmpty())
		{
			ServiceThread thread = (ServiceThread)parked.remove(0);
			synchronized(thread)
			{
				thread.terminated = true;
				thread.notify();
			}
		}
		
		// Todo: kill threads that don't terminate?
		// How to find zombies???
		// What about the current thread?
//		Thread.yield();
//		for(int i=0; i<pool.size(); i++) // Hack!!! Kill all threads.
//		{
//			Thread t = (Thread)pool.get(i);
//			if(!t.equals(Thread.currentThread()))
//			{
//				System.err.println("Threadpool: Killing blocked thread: "+t);
//				t.stop();
//			}
//		}
		
		// Aid cleanup
		group	= null;
		timer	= null;
	}
	
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public synchronized String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(SReflect.getInnerClassName(getClass()));
		buf.append("(poolsize=");
		buf.append(pool.size());
		buf.append(", running=");
		buf.append(running);
		buf.append(")");
		return buf.toString()+" "+hashCode();
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
			addThread();
		}
	}
	
	/**
	 * 
	 */
	protected synchronized void addThread()
	{
//		System.out.println("parked: "+parked.size());
		if(!parked.isEmpty())
		{
			ServiceThread thread = parked.remove(0);
			synchronized(thread)
			{
				((ServiceThread)thread).notified = true;
				thread.notify();
			}
		}
		else
		{
			ServiceThread thread = new ServiceThread();
			// Thread gets daemon state of parent, i.e. thread daemon state would
			// depend on called thread, which is not desired.
			thread.setDaemon(daemon);
//			thread.setDaemon(false);
			pool.add(thread);
			thread.start();
//			System.out.println("pool: "+toString()+" "+pool.size());
		}		
	}

//	/**
//	 *  Get a thread for a task.
//	 */
//	protected synchronized Thread getThread(Runnable task)
//	{
//		return (Thread)threads.get(task);
//	}

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

//		/** The start time. */
//		protected long start;
		
		protected boolean terminated;
		
		protected boolean notified;

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
			while(running && !terminated)
			{
				boolean exit = false;
				try
				{
					this.task = ((Runnable)tasks.dequeue(strategy.getWorkerTimeout()));
					Long start = enqueuetimes.remove(task);
					long now = System.currentTimeMillis();
					strategy.taskServed(now-start);
					
//					System.out.println("throuput: "+throughput);
					
//					this.task = ((Runnable)tasks.poll(strategy.getThreadTimeout(), TimeUnit.MILLISECONDS));
//					threads.put(task, this);
//					this.start = System.currentTimeMillis();
//					String	oldname	= this.getName();
//					this.setName(task.toString());

					try
					{
						if(task!=null)
							this.task.run();
					}
					catch(ThreadDeath e){}
//					catch(Throwable e)
//					{
//						e.printStackTrace();
//					}
//					this.setName(oldname);
				}
				catch(IBlockingQueue.ClosedException e)
				{
					assert	task == null;
					exit = true;
				}
				catch(TimeoutException e)
				{
					assert	task == null;
					exit = strategy.workerTimeoutOccurred();
				}
				catch(Exception e)
				{
//					task = null;
//					terminate	= true;
					e.printStackTrace();
				}
				
				if(task!=null)
				{
//					threads.remove(task);
					this.task = null;
					exit = strategy.taskFinished();
				}
				
//				System.out.println("exit: "+exit+" "+pool.size()+" "+parked.size()+" "+strategy);
				if(exit)
				{
					boolean	park;
					synchronized(ThreadPool.this)
					{
						park	= running && parked.size()<maxparked;
					}

					if(park)
					{
						park();
						
						synchronized(this)
						{
//							System.out.println("waiting for: "+strategy.getWorkerTimeout()*10);
							if(running)
							{
								try
								{
									wait(strategy.getWorkerTimeout());
								}
								catch(InterruptedException e)
								{
								}
							}
						}
						
						if(notified)
						{
							notified = false;
							unpark();
						}
						else
						{
							terminated = true;
//							System.out.println("thread removed (nothing to do): "+parked.size());
						}
					}
					else
					{
						terminated = true;
					}
				}
			}
			
			remove();
		}
		
		/**
		 * 
		 */
		protected void park()
		{
			synchronized(ThreadPool.this)
			{
//				System.out.println("readded: "+this);
				pool.remove(this);
				parked.add(this);
			}
		}
		
		/**
		 * 
		 */
		protected void unpark()
		{
			synchronized(ThreadPool.this)
			{
//				System.out.println("readded: "+this);
				pool.add(this);
				parked.remove(this);
			}
		}
		
		/**
		 * 
		 */
		protected void remove()
		{
//			System.out.println("thread terminating: "+this);
			
			terminated = true;
			
			synchronized(ThreadPool.this)
			{
//				System.out.println("readded: "+this);
				pool.remove(this);
				parked.remove(this);
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

		/**
		 *  Get the string representation.
		 */
		public String toString()
		{
			return super.toString()+":"+hashCode();
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
//		Thread t = new Thread(new Runnable()
//		{
//			public void run()
//			{
//				while(true)
//				{
//					System.out.println("alive");
//					try
//					{
//						Thread.sleep(1000);
//						Thread.currentThread().setDaemon(false);
//					}
//					catch(InterruptedException e)
//					{
//					}
//				}
//			}
//		});
//		t.setDaemon(true);
//		t.start();
//		
//		while(true)
//		{
//			System.out.println("main");
//			try
//			{
//				Thread.sleep(10000);
//			}
//			catch(InterruptedException e)
//			{
//			}
//		}
		
//		final ThreadPool tp	= new ThreadPool(new DefaultPoolStrategy(10, 100, 10000, 4));
//		int max = 10000;
//		todo = max;
//		final long start = System.currentTimeMillis();
//		for(int i=0; i<max; i++)
//		{
//			tp.execute(new Runnable()
//			{
//				int n = cnt++;
//				public void run()
//				{
//					String t = Thread.currentThread().toString();
//					System.out.println("a_"+this+" : "+t);
//					
//					long cnt = 0;
//					for(int i=0; i<1000000; i++)
//					{
//						cnt++;
//					}
//					
////					try{Thread.sleep(100);}
////					catch(InterruptedException e){}
//					System.out.println("b_"+this+" : "+t);
//					synchronized(tp)
//					{
//						todo--;
//						if(todo==0)
//							System.out.println("Execution finished. Needed: "+(System.currentTimeMillis()-start));
//					}
//				}
//
//				public String toString()
//				{
//					return "Task_"+n;
//				}
//			});
//		}
	}
	
	class TPTimerTask
	{
		
	}
}
