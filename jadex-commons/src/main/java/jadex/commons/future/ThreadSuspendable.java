package jadex.commons.future;


/**
 *  Suspendable for threads.
 */
public class ThreadSuspendable implements ISuspendable
{
	//-------- attributes --------
	
	/** The monitor. */
	protected Object monitor;
	
	/** The future. */
	protected IFuture<?>	future;
	
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
	public void suspend(IFuture<?> future, long timeout)
	{
		synchronized(monitor)
		{
			this.future	= future;
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
			finally
			{
				this.future	= null;
			}
		}
	}
	
	/**
	 *  Resume the execution of the suspendable.
	 */
	public void resume(IFuture<?> future)
	{
		synchronized(monitor)
		{
			// Only wake up if still waiting for same future (invalid resume might be called from outdated future after timeout already occurred).
			if(future==this.future)
			{
				monitor.notify();
			}
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
