package jadex.commons.future;


/**
 *  Suspendable for threads.
 */
public class ThreadSuspendable implements ISuspendable
{
	//-------- attributes --------
	
	/** The monitor. */
	protected Object monitor;
	
	//-------- constructors --------
	
	/**
	 *  Create a new suspendable.
	 */
	public ThreadSuspendable()
	{
		this(new Object());
	}
	
	/**
	 *  Create a new suspendable.
	 */
	public ThreadSuspendable(Object monitor)
	{
		this.monitor = monitor;
	}
	
	//-------- methods --------
	
	/**
	 *  Suspend the execution of the suspendable.
	 *  @param timeout The timeout.
	 */
	public void suspend(long timeout)
	{
		synchronized(monitor)
		{
			try
			{
				if(timeout>0)
				{
					monitor.wait(timeout);
				}
				else
				{
					monitor.wait();
				}
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}	
		}
	}
	
	/**
	 *  Resume the execution of the suspendable.
	 */
	public void resume()
	{
		synchronized(monitor)
		{
			monitor.notify();	
		}
	}
	
	/**
	 *  Get the monitor for waiting.
	 *  @return The monitor.
	 */
	public Object getMonitor()
	{
		return monitor;
	}
}
