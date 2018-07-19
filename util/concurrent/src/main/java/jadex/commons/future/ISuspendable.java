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
//	{
//		public void set(ISuspendable value) 
//		{
//			if(value instanceof ThreadSuspendable)
//				System.out.println("setting: "+value);
//			super.set(value);
//		}
//	};
	
	//-------- methods --------
	
	/**
	 *  Suspend the execution of the suspendable.
	 *  @param future The future to wait for.
	 *  @param timeout The timeout (-1 for no timeout, -2 for default timeout).
	 *  @param realtime Flag if timeout is realtime (in contrast to simulation time).
	 */
	public void suspend(Future<?> future, long timeout, boolean realtime);
	
	/**
	 *  Resume the execution of the suspendable.
	 *  @param future The future that issues the resume.
	 */
	public void resume(Future<?> future);
	
	/**
	 *  Get the monitor for waiting.
	 *  @return The monitor.
	 */
	public Object getMonitor();
}
