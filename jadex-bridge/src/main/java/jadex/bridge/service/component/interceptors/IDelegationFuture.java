package jadex.bridge.service.component.interceptors;

import jadex.commons.future.IResultListener;

/**
 *  Internal interface for delegating future to improve maintainability of delegating future implementations a little.
 *  Captures all basic methods of Future that needs to be overwritten in DelegatingFuture.
 *  Does not include derived methods that invoke other future methods and thus do not need to be redefined.
 */
public interface IDelegationFuture<E>
{
	//-------- data handling --------
	
	/**
	 *  Overwritten to change exception, if necessary.
	 */
	public boolean	doSetException(Exception exception, boolean undone);
	
	/**
	 *  Overwritten to change exception, if necessary.
	 */
	public boolean	doSetResult(E result, boolean undone);
	
	//-------- control flow handling --------
	
	/**
	 *  Notify a listener about the future result,
	 *  i.e. schedule listener notification in forward direction to caller.
	 */
	public void notifyListener(IResultListener<E> listener);
	
	/**
	 *  Process a forward command,
	 *  i.e. schedule command execution in forward direction (towards caller).
	 */
	public void sendForwardCommand(Object command);
	
	//--------  (move to future functionality) --------
	
	/**
	 *  Schedule forward in result direction,
	 *  i.e. from callee to caller,
	 *  e.g. update timer to avoid timeouts.
	 * /
	public void	scheduleForward(ICommand<Void> code);
	
	/**
	 *  Schedule backward in result direction,
	 *  i.e. from caller to callee,
	 *  e.g. future termination.
	 * /
	public void	scheduleBackward(ICommand<Void> code);
	*/
}
