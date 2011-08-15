package jadex.commons.future;

import jadex.commons.DebugException;


/**
 *  Result listener that delegates calls to a future.
 */
public class DelegationResultListener implements IResultListener
{
	//-------- attributes --------
	
	/** The future to which calls are delegated. */
	protected Future future;
	
//	protected DebugException	ex;
	
	//-------- constructors --------
	
	/**
	 *  Create a new listener.
	 */
	public DelegationResultListener(Future future)
	{
		this.future = future;
//		this.ex	= new DebugException();
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
//		System.err.println("Problem: "+exception);
		future.setException(exception);
	}
}
