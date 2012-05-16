package jadex.commons.concurrent;


/**
 *  This test class shows how the thread based scheduler works.
 *  The main thread represents the scheduler and schedules thread
 *  randomly.
 */
public class ThreadTest
{
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		new ThreadTest();
	}
	
	/**
	 *  Create a new thread test.
	 */
	public ThreadTest()
	{
		IThreadPool tp = ThreadPoolFactory.createThreadPool();
		MyTask[] tasks = new MyTask[10];
		MyMonitor[] monitors = new MyMonitor[tasks.length];
		for(int i=0; i<monitors.length; i++)
		{
			monitors[i] = new MyMonitor();
		}

		while(true)
		{
			int num = (int)((Math.random()*tasks.length));
			System.out.println("now scheduling: "+num);

			synchronized(monitors[num])
			{
              	monitors[num].setRunning(true);
				if(tasks[num]==null)
				{
					// It must be avoided that the new thread
					// immetiatly starts. Therefore its first
					// instruction is synchronized(monitor){}
					tasks[num] = new MyTask(""+num, monitors[num]);
					tp.execute(tasks[num]);
				}
				else
				{
					monitors[num].notify();
				}
				Thread.yield();
				System.out.println("sleeping scheduler");
				try
				{
					monitors[num].wait(2000);
				}
				catch(InterruptedException e)
				{
				}

				if(monitors[num].isRunning())
				{
					System.out.println("Thread was interrupted: "+num);
				}
			}
		}
	}

	/**
	 *  A task to execute in its own thread.
	 */
	class MyTask implements Runnable
	{
		/** The name. */
		protected String name;

		/** The pool monitor object. */
		protected MyMonitor monitor;

		/**
		 *  Create a new thread.
		 *  @param name The name.
		 *  @param monitor The monitor.
		 */
		public MyTask(String name, MyMonitor monitor)
		{
			this.name = name;
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
//			synchronized(monitor){};
			System.out.println("first woken up: "+this);
			while(true)
			{
				try
				{
					// Simulate some work.
					Thread.sleep(3000);

					synchronized(monitor)
					{
						// Notify the end of my work.
						System.out.println("sleeping: "+this);
						monitor.setRunning(false);
						monitor.notify();
						monitor.wait();
					}
				}
				catch(Exception e)
				{
				}
				System.out.println("woken up: "+this);
			}
		}

		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String toString()
		{
			return name;
		}
	}

	/**
	 *  A simple monitor that saves the thread state.
	 */
	class MyMonitor
	{
		/** The running state. */
		protected boolean running;

		/**
		 *  Create a new monitor.
		 */
		public MyMonitor()
		{
			this.running = true;
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
