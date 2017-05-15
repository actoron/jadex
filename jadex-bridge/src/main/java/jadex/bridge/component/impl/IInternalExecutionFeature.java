package jadex.bridge.component.impl;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;


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
	 *  Trigger component execution.
	 */
	public void	wakeup();

	/**
	 *  Do a step of a suspended component.
	 */
	public IFuture<Void> doStep(String stepinfo);

	/**
	 *  Block the current thread and allow execution on other threads.
	 *  @param monitor	The monitor to wait for.
	 *  @param realtime Flag if timeout is realtime (in contrast to simulation time).
	 */
	public void	block(final Object monitor, long timeout, boolean realtime);
	
	/**
	 *  Unblock the thread waiting for the given monitor
	 *  and cease execution on the current thread.
	 *  @param monitor	The monitor to notify.
	 */
	public void	unblock(Object monitor, Throwable exception);
	
	/**
	 *  Execute the executable.
	 *  @return True, if the object wants to be executed again.
	 */
	public boolean execute();
	
	/**
	 *  Add a synchronous subcomponent that will run on its parent's thread.
	 *  @param sub The subcomponent.
	 */
	public void addSubcomponent(IInternalExecutionFeature sub);

	/**
	 *  Remove a synchronous subcomponent.
	 *  @param sub The subcomponent.
	 */
	public void removeSubcomponent(IInternalExecutionFeature sub);
	
//	/**
//	 *  Test if the component's execution is currently at one of the
//	 *  given breakpoints. If yes, the component will be suspended by
//	 *  the platform.
//	 *  @param breakpoints	An array of breakpoints.
//	 *  @return True, when some breakpoint is triggered.
//	 */
//	public abstract boolean isAtBreakpoint(String[] breakpoints);
	
	/**
	 *  Get the step number when endstate began.
	 *  @return The step cnt.
	 */
	public int getEndstateStart();
	
}
