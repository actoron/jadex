package jadex.commons.future;


/**
 *  Result listener that delegates calls to a future.
 */
public abstract class ExceptionDelegationResultListener<E, T> implements IResultListener<E>
{
	//-------- attributes --------
	
	/** The future to which calls are delegated. */
	protected Future<T> future;
	
//	protected DebugException	ex;
	
	//-------- constructors --------
	
	/**
	 *  Create a new listener.
	 */
	public ExceptionDelegationResultListener(Future<T> future)
	{
		this.future = future;
//		this.ex	= new DebugException();
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
		catch(DuplicateResultException e)
		{
			if(e.getFuture()==future)
			{
				// Rethrow duplicate result exception to notify about usage error.
				throw e;
			}
			else
			{
				future.setExceptionIfUndone(e);				
			}
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			// Could happen that overridden customResultAvailable method
			// first sets result and then throws exception (listener ex are catched).
			future.setExceptionIfUndone(e);
		}
	}
	
	/**
	 *  Called when the result is available.
	 * @param result The result.
	 */
	public abstract void customResultAvailable(E result);
//	{
//		future.setResult(result);
//	}

	/**
	 *  Called when an exception occurred.
	 * @param exception The exception.
	 */
	public void exceptionOccurred(Exception exception)
	{
//		System.err.println("Problem: "+exception);
		future.setException(exception);
	}
}
