package jadex.commons.concurrent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


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

	/** The time slice (in ms). */
	protected long	timeslice;

	/** The tasks to do (executable->task). */
	protected Map	tasks;
	
	/** The executor for performing management operations. */
	protected Executor	executor;
	
	/** The sleep time (if sleeping is required before executing). */
	protected long	sleep;
	
	/** The last start time. */
	protected long	start;
	
	/** The number of currently running tasks. */
	protected int	concurrency;
	
	//-------- constructors --------
	
	/**
	 *  Create an execution service with default settings
	 *  (load=0.1, timeslice=50).
	 */
	public LoadManagingExecutionService(IThreadPool pool)
	{
		this(pool, 0.1, 50);
	}
	
	/**
	 *  Create an execution service with given settings.
	 */
	public LoadManagingExecutionService(IThreadPool pool, double load, long timeslice)
	{
		this.pool	= pool;
		this.load	= load;
		this.timeslice	= timeslice;
		this.tasks	= new HashMap();
		this.executor	= new Executor(pool, new IExecutable()
		{
			public boolean execute()
			{
				// Sleep before executing, to match desired CPU load.
				if(sleep>0)
				{
					try
					{
//						System.out.println("Sleeping: "+sleep);
						Thread.sleep(sleep);
					}
					catch (InterruptedException e){}
					sleep	= 0;
				}
				
				synchronized(LoadManagingExecutionService.this)
				{
					if(concurrency!=0)
						return false;	// Hack!!! execute can be called too often
						
					LoadManagingExecutionService.this.start	= System.nanoTime();
					LoadManagingExecutionService.this.concurrency	= 0;
					for(Iterator it=tasks.values().iterator(); it.hasNext(); )
					{
						Task	task	= (Task) it.next();
						LoadManagingExecutionService.this.pool.execute(task);
						concurrency++;
					}
//					System.out.println("Executing "+concurrency+" tasks.");
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
	public synchronized void	execute(IExecutable executable)
	{
		Task	task	= (Task)tasks.get(executable);
		if(task==null)
		{
			task	= new Task(executable);
			tasks.put(executable, task);
		}
		else
		{			
			// Set continue flag of existing task to avoid task being finished.
			task.setContinue(true);
		}

		// Concurrency==0 means not currently running -> start.
		if(concurrency==0)
		{
			executor.execute();
		}
	}
	
	/**
	 *  Called when a task has been performed once.
	 */
	public synchronized void	taskPerformed(Task task)
	{
		if(task.isFinished())
		{
			tasks.remove(task);
		}

		concurrency--;
		if(concurrency==0 && !tasks.isEmpty())
		{
			long	time	= System.nanoTime() - start;
			sleep	= (long)((time/load - time) / 1000000);
			executor.execute();
//			System.out.println("Execution finished in "+time/1000000+" millis.");
		}
	}
	
	//-------- helper classes --------
	
	/**
	 *  A task info holds a task and meta information.
	 */
	public class Task	implements Runnable
	{
		//-------- attributes --------
		
		/** The task. */
		protected IExecutable	executable;
		
		/** Flag indicating the task is finished. */
		protected boolean	finished;
		
		/** Flag indicating that the task has been registered again. */
		protected boolean	cont;
		
		//-------- constructors --------
		
		/**
		 *  Create a new task info for a given task.
		 */
		public Task(IExecutable task)
		{
			this.executable	= task;
		}
		
		//-------- methods --------

		/**
		 *  Perform the task once and notify the manager.
		 */
		public void	run()
		{
			finished	= !executable.execute() && !cont;
			LoadManagingExecutionService.this.taskPerformed(this);
		}
		
		/**
		 *  Check if the task is done.
		 */
		public boolean isFinished()
		{
			return finished;
		}
		
		/**
		 *  Set the continue flag.
		 */
		public void	setContinue(boolean cont)
		{
			this.cont	= cont;
		}
		
		/**
		 *  Create a string representation of the task info.
		 */
		public String	toString()
		{
			return "Task("+executable+", finished="+finished+")";
		}
	}

	//-------- main for testing --------
	
	public static void main(String[] args)
	{
		LoadManagingExecutionService	service	= new LoadManagingExecutionService(
			ThreadPoolFactory.createThreadPool(), 1, 50);
		service.execute(new TestExecutable());
		service.execute(new TestExecutable());
		service.execute(new TestExecutable());
		service.execute(new TestExecutable());
		service.execute(new TestExecutable());
		service.execute(new TestExecutable());
		service.execute(new TestExecutable());
		service.execute(new TestExecutable());
	}
	
	static class TestExecutable	implements IExecutable
	{
		public boolean execute()
		{
			double	sum	= 0;
			for(int i=0; i<10000000; i++)
				sum+=i;
			return true;
		}
	}
}
