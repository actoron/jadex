package jadex.commons.future;


/**
 *  Result listener that delegates calls to a future
 *  and can be called from remote.
 */
public class RemoteDelegationResultListener<E> implements IRemoteResultListener<E>
{
	//-------- attributes --------
	
	/** The future to which calls are delegated. */
	protected Future<E> future;
	
	//-------- constructors --------
	
	/**
	 *  Create a new listener.
	 */
	public RemoteDelegationResultListener(Future<E> future)
	{
		this.future = future;
	}
	
	//-------- methods --------
	
	/**
	 *  Called when the result is available.
	 * @param result The result.
	 */
	public final void resultAvailable(E result)
	{
		try
		{
			customResultAvailable(result);
		}
		catch(Exception e)
		{
			// Could happen that overridden customResultAvailable method
			// first sets result and then throws exception (listener ex are catched).
			future.setExceptionIfUndone(e);
		}
	}
	
	/**
	 *  Called when the result is available.
	 * @param result The result.
	 */
	public void customResultAvailable(E result)
	{
		future.setResult(result);
	}

	/**
	 *  Called when an exception occurred.
	 * @param exception The exception.
	 */
	public void exceptionOccurred(Exception exception)
	{
		future.setException(exception);
	}
}
