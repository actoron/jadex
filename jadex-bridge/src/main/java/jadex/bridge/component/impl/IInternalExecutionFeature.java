package jadex.bridge.component.impl;


/**
 *  The execution feature allows to schedule steps
 *  to be synchronously executed on the component.
 */
public interface IInternalExecutionFeature
{
	/**
	 *  Block the current thread and allow execution on other threads.
	 *  @param monitor	The monitor to wait for.
	 */
	public void	block(final Object monitor, long timeout);
	
	/**
	 *  Unblock the thread waiting for the given monitor
	 *  and cease execution on the current thread.
	 *  @param monitor	The monitor to notify.
	 */
	public void	unblock(Object monitor, Throwable exception);
}
