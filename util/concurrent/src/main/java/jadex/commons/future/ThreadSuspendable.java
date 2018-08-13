package jadex.commons.future;

import jadex.commons.SUtil;
import jadex.commons.TimeoutException;
import jadex.commons.concurrent.ThreadPool;

/**
 *  Suspendable for threads.
 */
public class ThreadSuspendable extends ThreadLocalTransferHelper implements ISuspendable
{
	//-------- attributes --------
	
	/** The future. */
	protected IFuture<?> future;
	
	/** The resumed flag to differentiante from timeout.*/
	protected boolean	resumed;
	
	//-------- methods --------
	
	/**
	 *  Suspend the execution of the suspendable.
	 *  @param timeout The timeout.
	 *  @param realtime Flag if timeout is realtime (in contrast to simulation time).
	 */
	public void suspend(Future<?> future, long timeout, boolean realtime)
	{
		if(timeout==Future.UNSET)
			timeout = getDefaultTimeout();
		
		synchronized(this)
		{
			this.future	= future;
			this.resumed	= false;
			assert !ThreadPool.WAITING_THREADS.containsKey(Thread.currentThread());
			ThreadPool.WAITING_THREADS.put(Thread.currentThread(), future);
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
				assert ThreadPool.WAITING_THREADS.get(Thread.currentThread())==future;
				ThreadPool.WAITING_THREADS.remove(Thread.currentThread());
				// Restore the thread local values after switch
				afterSwitch();
				this.future	= null;
			}
			
			if(!resumed)
			{
				throw new TimeoutException(("Timeout: "+timeout+", realtime="+realtime));
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
				resumed	= true;
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
	protected long getDefaultTimeout()
	{
		return SUtil.DEFTIMEOUT;
	}
}
