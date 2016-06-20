package jadex.bridge.service.component.interceptors;

/**
 *  Internal interface for delegating future to improve maintainability of delegating future implementations a little.
 *  Captures all basic methods of Future that needs to be overwritten in DelegatingFuture.
 *  Does not include derived methods that invoke other future methods and thus do not need to be redefined.
 */
public interface ITerminableDelegationFuture<E>	extends IDelegationFuture<E>
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
	 *  Process a backward command,
	 *  i.e. schedule command execution in backward direction (towards callee).
	 */
	public void sendForwardCommand(Object command);
}
