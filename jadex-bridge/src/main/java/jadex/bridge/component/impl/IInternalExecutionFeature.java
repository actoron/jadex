package jadex.bridge.component.impl;

import jadex.bridge.IInternalAccess;


/**
 *  The execution feature allows to schedule steps
 *  to be synchronously executed on the component.
 */
public interface IInternalExecutionFeature
{
	//-------- constants -------- 
	
	/** The currently executing component (if any). */
	// Provided for fast caller/callee context-switching avoiding to use cms.
	public static final ThreadLocal<IInternalAccess>	LOCAL	= new ThreadLocal<IInternalAccess>();
	
	//-------- methods --------
	
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
