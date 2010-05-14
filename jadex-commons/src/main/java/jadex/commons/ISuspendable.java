package jadex.commons;

/**
 *  Interface for suspendable entities.
 *  Is used by the IFuture to suspend callers.
 */
public interface ISuspendable
{
	/**
	 *  Suspend the execution of the suspendable.
	 *  @param timeout The timeout.
	 */
	public void suspend(long timeout);
	
	/**
	 *  Resume the execution of the suspendable.
	 */
	public void resume();
}
