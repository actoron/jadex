package jadex.commons.future;


/**
 *  Result listener that delegates calls to a future.
 */
public class DelegationResultListener<E> implements IResultListener<E>
{
	//-------- attributes --------
	
	/** The future to which calls are delegated. */
	protected Future<E> future;
	
	/** Flag if undone methods should be used. */
	protected boolean undone;
	
//	protected DebugException	ex;
	
	//-------- constructors --------
	
	/**
	 *  Create a new listener.
	 */
	public DelegationResultListener(Future<E> future)
	{
		this(future, false);
	}
	
	/**
	 *  Create a new listener.
	 */
	public DelegationResultListener(Future<E> future, boolean undone)
	{
		this.future = future;
//		this.ex	= new DebugException();
		this.undone = undone;
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
	public void customResultAvailable(E result)
	{
		if(undone)
		{
			future.setResultIfUndone(result);
		}
		else
		{
			future.setResult(result);
		}
	}

	/**
	 *  Called when an exception occurred.
	 * @param exception The exception.
	 */
	public void exceptionOccurred(Exception exception)
	{
//		System.err.println("Problem: "+exception);
		if(undone)
		{
			future.setExceptionIfUndone(exception);
		}
		else
		{
			future.setException(exception);
		}
	}
}
