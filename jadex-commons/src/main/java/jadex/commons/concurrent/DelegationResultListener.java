package jadex.commons.concurrent;

import jadex.commons.Future;

/**
 *  Result listener that delegates calls to a future.
 */
public class DelegationResultListener implements IResultListener
{
	//-------- attributes --------
	
	/** The future to which calls are delegated. */
	protected Future future;
	
	//-------- constructors --------
	
	/**
	 *  Create a new listener.
	 */
	public DelegationResultListener(Future future)
	{
		this.future = future;
	}
	
	//-------- methods --------
	
	/**
	 *  Called when the result is available.
	 *  @param source The source component.
	 *  @param result The result.
	 */
	public final void resultAvailable(Object source, Object result)
	{
		future.setResult(result);
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param source The source component.
	 *  @param exception The exception.
	 */
	public final void exceptionOccurred(Object source, Exception exception)
	{
		future.setException(exception);
	}
}
