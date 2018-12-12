package jadex.commons.concurrent;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


/**
 *  This class allows to perform background operations with adjustable
 *  CPU utilization.
 */
public class LoadManagingExecutionService
{
	//-------- attributes --------

	/** The thread pool. */
	protected IThreadPool	pool;

	/** The desired CPU load. */
	protected double	load;

	/** The max time slice (in ms). Only met if single tasks are below this time. */
	protected long	timeslice;

	/** The tasks to do. */
	protected Set	tasks;
	
	/** The executor for performing management operations. */
	protected Executor	executor;
	
	/** A limit for concurrency (to avoid exceeding the timeslice). */
	protected int	limit;
	
	//-------- transient attributes (set during one execution cycle) --------
	
//	/** The sleep time (if sleeping is required before executing). */
//	protected long	sleep;
	
	/** The last start time. */
	protected long	start;
	
	/** The number of currently running tasks. */
	protected int	concurrency;
	
	//-------- constructors --------
	
	/**
	 *  Create an execution service with default settings
	 *  (timeslice=50).
	 */
	public LoadManagingExecutionService(IThreadPool pool)
	{
		this(pool, 50);
	}
	
	/**
	 *  Create an execution service with given settings.
	 */
	public LoadManagingExecutionService(IThreadPool threadpool, long timeslice)
	{
		this.pool	= threadpool;
		this.timeslice	= timeslice;
		this.limit	= 1;
		this.tasks	= new TreeSet();
		this.executor	= new Executor(pool, new IExecutable()
		{
			public boolean execute()
			{
				try
				{
					// Sleep before executing, to match desired CPU load.
//					if(sleep>0)
//					{
						try
						{
//							System.out.println("Sleeping: "+(long)(LoadManagingExecutionService.this.timeslice*(1-load)));
//							if(sleep>1000)
//							{
//								System.out.println("Sleep warning: "+sleep);
//								sleep	= 1000;
//							}
							Thread.sleep((long)(LoadManagingExecutionService.this.timeslice*(1-load)));
						}
						catch (InterruptedException e){}
//						sleep	= 0;
//					}
					
					synchronized(LoadManagingExecutionService.this)
					{
						if(concurrency!=0)
							return false;	// Hack!!! execute can be called too often
							
						start	= System.nanoTime();	// accuracy of System.currentTimeMillis() only about 16ms (Sun JVM / Windows)
						concurrency	= 0;
						load	= 0.0;
						for(Iterator it=tasks.iterator(); concurrency<limit && it.hasNext(); )
						{
							Task	task	= (Task)it.next();
							if(load==0.0)
							{
								load	= task.priority;
							}
	
							if(load==task.priority)
							{
								it.remove();
								concurrency++;
								pool.execute(task);
							}
							else
							{
								break;
							}
						}
						if(concurrency>0)
						{
							limit	= concurrency;
//							System.out.println("Executing "+concurrency+" tasks with load "+load);
						}
					}
				}
				catch(Exception e)
				{
					// Can happen when threadpool is already shut down.
				}
				return false;
			}
		});
	}
	
	//------- methods --------
	
	/**
	 *  Execute a task. Triggers the task to
	 *  be executed in future. 
	 *  @param executable The task to execute.
	 *  @param listener Called when execution has started.
	 */
	public synchronized void	execute(IExecutable executable, double priority)
	{
		tasks.add(new Task(executable, priority));

		// Concurrency==0 means not currently running -> start.
		if(concurrency==0)
		{
			try
			{
				executor.execute();
			}
			catch(Exception e)
			{
				// ignore, can only be caused by terminated threadpool service.
			}
		}
	}
	
	/**
	 *  Called when a task has been performed once.
	 */
	protected synchronized void	taskPerformed(Task task)
	{
		concurrency--;
		if(concurrency==0)
		{
			// Calculate sleep time to meet load setting.
			long	time	= System.nanoTime() - start;
//			sleep	= (long)((time/load - time) / 1000000);
			
			// Calculate concurrency limit to meet max timeslice setting:
//			double	lastslice	= Math.max(sleep + time/1000000, 0.1);
//			double	newlimit	= limit * timeslice / lastslice;
			double	newlimit	= limit * (timeslice*load*1000000) / time;
			// Use exponential annealing to avoid oscillations
			limit	= Math.max(1, limit + (int)(0.5*(newlimit-limit)));
			
//			System.out.println("Execution finished in "+(time/100000)/10.0+" millis. New limit: "+limit);

			if(!tasks.isEmpty())
			{
				try
				{
					executor.execute();
				}
				catch(Exception e)
				{
					// Ignore when threadpool is shut down.
				}
			}
//			else
//			{
//				sleep	= 0; 	// do not sleep, when re starting after idle time.
//			}
		}
	}
	
	//-------- helper classes --------
	
	/** The counter. */
	protected static int	COUNTER	= 0;

	/**
	 *  A task info holds a task and meta information.
	 */
	public class Task	implements Runnable, Comparable
	{		
		//-------- attributes --------
		
		/** The task. */
		protected IExecutable	executable;
		
		/** The priority. */
		protected double	priority;
		
		/** The sequence number. */
		protected int	seqnr;
		
		//-------- constructors --------
		
		/**
		 *  Create a new task info for a given task.
		 */
		public Task(IExecutable task, double priority)
		{
			this.executable	= task;
			this.priority	= priority;
			synchronized(Task.class)
			{
				this.seqnr	= COUNTER++;
			}
		}
		
		//-------- Runnable interface --------

		/**
		 *  Perform the task once and notify the manager.
		 */
		public void	run()
		{
			boolean	execute	= false;
			try
			{
//				System.out.println("Executing: "+this);
				execute	= executable.execute();
			}
			catch(RuntimeException e)
			{
				e.printStackTrace();
			}
			synchronized(LoadManagingExecutionService.this)
			{
				if(execute)
				{
					LoadManagingExecutionService.this.execute(executable, priority);
				}
				LoadManagingExecutionService.this.taskPerformed(this);
			}
		}
		
		//-------- Comparable interface --------

		/**
	     *  Return a negative integer, zero, or a positive integer as this object
	     *		is less than, equal to, or greater than the specified object.
	     */
		public int compareTo(Object obj)
		{
			double	ret	= -1;
			if(obj instanceof Task)
			{
				if(priority!=((Task)obj).priority)
					ret	= ((Task)obj).priority - priority;
				else
					ret	= seqnr - ((Task)obj).seqnr;
			}
			return ret>0 ? 1 : ret<0 ? -1 : 0;
		}
		
		//-------- methods --------
		
		/**
		 *  Create a string representation of the task info.
		 */
		public String	toString()
		{
			return "Task("+executable+", seqnr="+seqnr+", priority="+priority+")";
		}
	}

	//-------- main for testing --------
	
	public static void main(String[] args)
	{
		double	PRIORITY	= 0.1;
		LoadManagingExecutionService	service	= new LoadManagingExecutionService(
			ThreadPoolFactory.createThreadPool());
		service.execute(new TestExecutable(), PRIORITY);
		service.execute(new TestExecutable(), PRIORITY);
		service.execute(new TestExecutable(), PRIORITY);
		service.execute(new TestExecutable(), PRIORITY+0.3);
		service.execute(new TestExecutable(), PRIORITY+0.3);
		service.execute(new TestExecutable(), PRIORITY+0.3);
		service.execute(new TestExecutable(), PRIORITY+0.8);
		service.execute(new TestExecutable(), PRIORITY+0.8);
	}
	
	static class TestExecutable	implements IExecutable
	{
		int cnt	= 1;
		public boolean execute()
		{
			double	sum	= 0;
			for(int i=0; i<5000000; i++)
				sum+=i;
			System.out.println("Executed "+this+", "+cnt);
			return cnt++<10;
		}
	}
}
