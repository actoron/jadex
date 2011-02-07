package jadex.commons.future;



/**
 *  Intermediate version of the delegation result listener.
 */
public class IntermediateDelegationResultListener implements IIntermediateResultListener
{
	//-------- attributes --------
	
	/** The future to which calls are delegated. */
	protected IntermediateFuture future;
	
	//-------- constructors --------
	
	/**
	 *  Create a new listener.
	 */
	public IntermediateDelegationResultListener(IntermediateFuture future)
	{
		this.future = future;
	}
	
	//-------- methods --------
	
	/**
	 *  Called when the result is available.
	 * @param result The result.
	 */
	public final void resultAvailable(Object result)
	{
		try
		{
			customResultAvailable(result);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			// Could happen that overridden customResultAvailable method
			// first sets result and then throws exception (listener ex are catched).
			future.setExceptionIfUndone(e);
		}
	}
	
	/**
	 *  Called when an intermediate result is available.
	 *  @param result The result.
	 */
	public void intermediateResultAvailable(Object result)
	{
		try
		{
			customIntermediateResultAvailable(result);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			// Could happen that overridden customResultAvailable method
			// first sets result and then throws exception (listener ex are catched).
			future.setExceptionIfUndone(e);
		}
	}
	
	/**
     *  Declare that the future is finished.
     */
    public void finished()
    {
    	future.setFinished();
    }
	
	/**
	 *  Called when the result is available.
	 * @param result The result.
	 */
	public void customResultAvailable(Object result)
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
	
	/**
	 *  Called when an intermediate result is available.
	 * @param result The result.
	 */
	public void customIntermediateResultAvailable(Object result)
	{
		future.addIntermediateResult(result);
	}
}
