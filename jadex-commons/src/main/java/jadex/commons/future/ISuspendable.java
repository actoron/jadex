package jadex.commons.future;


/**
 *  Interface for suspendable entities.
 *  Is used by the IFuture to suspend callers.
 */
public interface ISuspendable
{
	//-------- constants --------
	
	/** The component suspendable for a component thread. */
	public static final ThreadLocal<ISuspendable>	SUSPENDABLE	= new ThreadLocal<ISuspendable>();
	
	//-------- methods --------
	
	/**
	 *  Suspend the execution of the suspendable.
	 *  @param future	The future to wait for.
	 *  @param timeout The timeout.
	 */
	public void suspend(IFuture<?> future, long timeout);
	
	/**
	 *  Resume the execution of the suspendable.
	 *  @param future	The future that issues the resume.
	 */
	public void resume(IFuture<?> future);
	
	/**
	 *  Get the monitor for waiting.
	 *  @return The monitor.
	 */
	public Object getMonitor();
}
