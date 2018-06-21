package jadex.commons.concurrent;

import jadex.commons.collection.BlockingQueue;
import jadex.commons.collection.IBlockingQueue;

/**
 *  This test class shows how the thread based scheduler works.
 *  The main thread represents the scheduler and schedules thread
 *  randomly.
 */
public class MultiThreadTest
{
	//-------- attributes --------

	/** The waiting pool. */
	protected static IBlockingQueue<ExecutionThread> waits;

	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		waits = new BlockingQueue<ExecutionThread>();
		Thread[] threads = new Thread[3];
		MyMonitor[] monitors = new MyMonitor[threads.length];
		for(int i=0; i<monitors.length; i++)
		{
			monitors[i] = new MyMonitor();
		}

		// start all pool
		for(int i=0; i<threads.length; i++)
		{
			System.out.println("now starting: "+i);
          		monitors[i].setRunning(true);
			if(threads[i]==null)
			{
				threads[i] = new ExecutionThread(""+i, monitors[i]);
				threads[i].start();
			}
			else
			{
				synchronized(monitors[i])
				{
					monitors[i].notify();
				}
			}
		}

		while(true)
		{
			ExecutionThread et	= waits.dequeue();
			MyMonitor monitor = et.getMonitor();
			System.out.println("Restarting: "+et);
			// wait till thread waits.
			synchronized(monitor)
			{
				monitor.setRunning(true);
				et.getMonitor().notify();
			}			
		}
	}

	/**
	 *  A thread that
	 */
	static class ExecutionThread extends Thread
	{
		/** The pool monitor object. */
		protected MyMonitor monitor;

		/**
		 *  Create a new thread.
		 *  @param name The name.
		 *  @param monitor The monitor.
		 */
		public ExecutionThread(String name, MyMonitor monitor)
		{
			super(name);
			this.monitor = monitor;
		}

		/**
		 *  The pool tasks are:
		 *  a) wait till the scheduler sleeps and my monitor is free.
		 *  b) do my work.
		 *  c) claim my monitor, set the state, wake up the scheduler and wait.
		 *  (The scheduler needs the lock on the monitor)
		 */
		public void run()
		{
//			System.out.println("here: "+this);
			//while(true)
			//{
				// Simulate some work.
				System.out.println("a: (some work)"+this);		
				try{Thread.sleep(1000);}
				catch(Exception e){}

				// Simulate a wait for.
				System.out.println("b: (pause): "+this);
				synchronized(monitor)
				{
					monitor.setRunning(false);		
					try
					{
						waits.enqueue(this);
						monitor.wait();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}

				// Simulate some work.
				System.out.println("c: (some other work)"+this);		
				try{Thread.sleep(1000);}
				catch(Exception e){}
			
				System.out.println("finished: "+this);
			//}
		}

		/**
		 *  Get the monitor.
		 */ 
		protected MyMonitor getMonitor()
		{
			return monitor;
		}
	}

	/**
	 *  A simple monitor that saves the thread state.
	 */
	static class MyMonitor
	{
		/** The running state. */
		protected boolean running;

		/**
		 *  Create a new monitor.
		 */
		public MyMonitor()
		{
			this.running = false;
		}

		/**
		 *  Get the running state.
		 *  @return The running state.
		 */
		public boolean isRunning()
		{
			return running;
		}

		/**
		 *  Set the running state.
		 *  @param running
		 */
		public void setRunning(boolean running)
		{
			this.running = running;
		}
	}
}



