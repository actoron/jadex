package jadex.commons.future;


/**
 *  Suspendable for threads.
 */
public class ThreadSuspendable extends ThreadLocalTransferHelper implements ISuspendable
{
	//-------- attributes --------
	
	/** The future. */
	protected IFuture<?> future;
	
	//-------- methods --------
	
	/**
	 *  Suspend the execution of the suspendable.
	 *  @param timeout The timeout.
	 */
	public void suspend(Future<?> future, long timeout)
	{
		if(timeout==-2)//Timeout.UNSET)
			timeout = getDefaultTimeout();
		
		synchronized(this)
		{
			this.future	= future;
			try
			{
				if(timeout>0)
				{
					this.wait(timeout);
				}
				else
				{
					this.wait();
				}
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			finally
			{
				// Restore the thread local values after switch
				afterSwitch();
				this.future	= null;
			}
		}
	}
	
	/**
	 *  Resume the execution of the suspendable.
	 */
	public void resume(Future<?> future)
	{
		synchronized(this)
		{
			// Only wake up if still waiting for same future (invalid resume might be called from outdated future after timeout already occurred).
			if(future==this.future)
			{
				// Save the thread local values before switch
				beforeSwitch();
				this.notify();
			}
		}
	}
	
	/**
	 *  Get the monitor for waiting.
	 *  @return The monitor.
	 */
	public Object getMonitor()
	{
		return this;
	}
	
	/**
	 *  Get the default timeout.
	 *  @return The default timeout (-1 for none).
	 */
	public long getDefaultTimeout()
	{
		return -1;
	}
}
