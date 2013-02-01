package jadex.commons.future;


/**
 *  Suspendable for threads.
 */
public class ThreadSuspendable implements ISuspendable
{
	//-------- attributes --------
	
	/** The future. */
	protected IFuture<?>	future;
	
	//-------- methods --------
	
	/**
	 *  Suspend the execution of the suspendable.
	 *  @param timeout The timeout.
	 */
	public void suspend(IFuture<?> future, long timeout)
	{
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
				this.future	= null;
			}
		}
	}
	
	/**
	 *  Resume the execution of the suspendable.
	 */
	public void resume(IFuture<?> future)
	{
		synchronized(this)
		{
			// Only wake up if still waiting for same future (invalid resume might be called from outdated future after timeout already occurred).
			if(future==this.future)
			{
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
}
