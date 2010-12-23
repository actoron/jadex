package jadex.commons;


/**
 * 
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
	 *  @param source The source component.
	 *  @param result The result.
	 */
	public final void resultAvailable(Object source, Object result)
	{
		try
		{
			customResultAvailable(source, result);
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
	 *  @param source The source component.
	 *  @param result The result.
	 */
	public void intermediateResultAvailable(Object source, Object result)
	{
		try
		{
			customIntermediateResultAvailable(source, result);
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
	 *  Called when the result is available.
	 *  @param source The source component.
	 *  @param result The result.
	 */
	public void customResultAvailable(Object source, Object result)
	{
		future.setResult(result);
	}

	/**
	 *  Called when an exception occurred.
	 *  @param source The source component.
	 *  @param exception The exception.
	 */
	public void exceptionOccurred(Object source, Exception exception)
	{
		future.setException(exception);
	}
	
	/**
	 *  Called when an intermediate result is available.
	 *  @param source The source component.
	 *  @param result The result.
	 */
	public void customIntermediateResultAvailable(Object source, Object result)
	{
		future.addIntermediateResult(result);
	}
}
