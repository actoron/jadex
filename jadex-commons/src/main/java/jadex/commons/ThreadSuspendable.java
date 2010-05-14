package jadex.commons;

/**
 * 
 */
public class ThreadSuspendable implements ISuspendable
{
	/** The monitor. */
	protected Object monitor;
	
	/**
	 * 
	 */
	public ThreadSuspendable(Object monitor)
	{
		this.monitor = monitor;
	}
	
	/**
	 * 
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
				throw new RuntimeException(e);
			}	
		}
	}
	
	/**
	 * 
	 */
	public void resume()
	{
		synchronized(monitor)
		{
			monitor.notify();	
		}
	}
}
