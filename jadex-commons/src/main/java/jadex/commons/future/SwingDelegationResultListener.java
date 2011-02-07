package jadex.commons.future;

/**
 *  Delegation result listener that calls customResultAvailable and
 *  customExceptionOccurred on swing thread.
 */
public class SwingDelegationResultListener extends SwingDefaultResultListener
{
	//-------- attributes --------
	
	/** The future to which calls are delegated. */
	protected Future future;
	
	//-------- constructors --------
	
	/**
	 *  Create a new listener.
	 */
	public SwingDelegationResultListener(Future future)
	{
		this.future = future;
	}
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void customResultAvailable(Object result)
	{
		future.setResult(result);
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void customExceptionOccurred(Exception exception)
	{
		future.setException(exception);
	}
}
