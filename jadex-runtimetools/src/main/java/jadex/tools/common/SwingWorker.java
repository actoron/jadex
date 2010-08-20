package jadex.tools.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.Timer;


/**
 *  This class allows to perform background operations on the swing thread
 */
public class SwingWorker	extends Timer implements ActionListener
{
	//-------- constants --------
	
	/** The time slice (in ms). */
	protected static final int	TIME_SLICE	= 50;
		
	//-------- attributes --------

	/** The list of tasks to do. */
	protected Set	tasks;
	
	//-------- constructors --------
	
	/**
	 *  Create the worker singleton.
	 */
	protected SwingWorker()
	{
		super((int)(TIME_SLICE*1.2), null);	// Keep about 20% CPU time
		this.addActionListener(this);
		this.tasks	= new HashSet();
	}
	
	//------- ActionListener interface --------
	
	/**
	 *  Called from the timer.
	 */
	public void actionPerformed(ActionEvent arg0)
	{
		// Starttime (for debugging)
//		long starttime	= System.currentTimeMillis();
		
		// Execute all tasks.
		boolean	removed	= false;
		Iterator	it	= tasks.iterator();
		while(it.hasNext())
		{
			try
			{
				TaskInfo	task	= (TaskInfo)it.next();
				task.execute();
				if(task.isFinished())
				{
					removed	= true;
					it.remove();
				}
			}
			catch(RuntimeException e)
			{
				e.printStackTrace();
				if(!removed)
				{
					removed	= true;
					it.remove();
				}
			}
		}

		// Update slices when some tasks have been removed.
		if(removed)
		{
			if(tasks.isEmpty())
				SwingWorker.this.stop();
			else
				calculateSlices();
		}

//		System.out.println("worker end: "+(System.currentTimeMillis()-starttime));
	}

	//-------- methods --------

	/**
	 *  Calculate slices for the current tasks.
	 */
	protected void calculateSlices()
	{
		double	total	= 0;
		for(Iterator i=tasks.iterator(); i.hasNext(); )
		{
			total	+= ((TaskInfo)i.next()).getPriority();
		}
		double	adjust	= total>1 ? total : 1;
		for(Iterator i=tasks.iterator(); i.hasNext(); )
		{
			TaskInfo	task	= (TaskInfo)i.next();
			task.setSlice((long)(TIME_SLICE*task.getPriority()/adjust));
		}
//		System.out.println("Tasks: "+tasks);
	}

	/**
	 *  Add a task.
	 */
	protected void addTask(TaskInfo ti)
	{
		if(worker.tasks.add(ti))
		{
			worker.calculateSlices();
			worker.restart();
		}
	}

	//-------- static part --------

	/** The singleton instance. */
	protected static SwingWorker	worker	= new SwingWorker();
	
	/**
	 *  Add a task
	 */
	public static void	addTask(Task task, double priority)
	{
		final TaskInfo	ti	= new TaskInfo(task, priority);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				worker.addTask(ti);
			}
		});
	}
	
	//-------- helper classes --------
	
	/**
	 *  A task info holds a task and meta information.
	 */
	public static class TaskInfo
	{
		//-------- attributes --------
		
		/** The task. */
		protected Task	task;
		
		/** The task priority. */
		protected double	priority;
		
		/** The time slice. */
		protected long	slice;
		
		/** Flag indicating the task is finished. */
		protected boolean	finished;
		
		//-------- constructors --------
		
		/**
		 *  Create a new task info for a given task.
		 */
		public TaskInfo(Task task, double priority)
		{
			this.task	= task;
			this.priority	= priority;
		}
		
		//-------- methods --------
		
		/**
		 *  Get the priority of the task.
		 */
		public double getPriority()
		{
			return priority;
		}
		
		/**
		 *  Set the slice of the task.
		 */
		public void setSlice(long slice)
		{
			this.slice	= slice;
		}

		/**
		 *  Execute the task as long as its current slice.
		 */
		public void	execute()
		{
			long starttime	= System.currentTimeMillis();
			// Use additional counter for slices<10ms due to inaccuracy of System.currentTimeMillis().
			for(int i=0; (i<slice || slice>10) && !finished && System.currentTimeMillis()-starttime<slice; i++)
			{
				finished	= !task.execute();
			}
//			System.out.println("task end (slice="+slice+"): "+(System.currentTimeMillis()-starttime));
		}
		
		/**
		 *  Check if the task is done.
		 */
		public boolean isFinished()
		{
			return finished;
		}
		
		/**
		 *  Create a string representation of the task info.
		 */
		public String	toString()
		{
			return "Task("+task+", priority="+priority+", slice="+slice+")";
		}
	}

	/**
	 *  A task is executed until it is finished.
	 */
	public interface Task
	{
		/**
		 *  Execute the task.
		 *  @return true, when the task continues (i.e. is not finished).
		 */
		public boolean	execute();
	}

	//-------- main for testing --------
	
	public static void main(String[] args)
	{
//		addTask(new Task()
//		{
//			public boolean execute()
//			{
//				try{Thread.sleep(10);}catch(InterruptedException e){}
//				System.out.println("Here is task 1");
//				return true;
//			}
//		}, 0.8);
		addTask(new Task()
		{
			public boolean execute()
			{
				try{Thread.sleep(10);}catch(InterruptedException e){}
				System.out.println("Here is task 2");
				return true;
			}
		}, 0.1);
//		addTask(new Task()
//		{
//			public boolean execute()
//			{
//				try{Thread.sleep(10);}catch(InterruptedException e){}
//				System.out.println("Here is task 3");
//				return true;
//			}
//		}, 0.3);
	}
}
