package jadex.commons.future;

import java.util.Collection;

/**
 * 
 */
public abstract class IntermediateExceptionDelegationResultListener<E, T> implements IIntermediateResultListener<E>
{
//-------- attributes --------
	
	/** The future to which calls are delegated. */
	protected IntermediateFuture<T> future;
	
//	protected DebugException	ex;
	
	//-------- constructors --------
	
	/**
	 *  Create a new listener.
	 */
	public IntermediateExceptionDelegationResultListener(IntermediateFuture<T> future)
	{
		this.future = future;
//		this.ex	= new DebugException();
	}
	
	//-------- methods --------
	
	/**
	 *  Called when the result is available.
	 *  This method is only called for non-intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method has not been called.
	 *  @param result The final result.
	 */
	public void resultAvailable(Collection<E> result)
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
	//		e.printStackTrace();
			// Could happen that overridden customResultAvailable method
			// first sets result and then throws exception (listener ex are catched).
			future.setExceptionIfUndone(e);
		}
	}
	
//	/**
//	 *  Called when an exception occurred.
//	 *  @param exception The exception.
//	 */
//	public void exceptionOccurred(Exception exception);
	
	/**
	 *  Called when an intermediate result is available.
	 *  @param result The result.
	 */
	public abstract void intermediateResultAvailable(E result);
	
	/**
     *  Declare that the future is finished.
	 *  This method is only called for intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method was called for all
	 *  intermediate results before.
     */
    public void finished()
    {
    	future.setFinished();
    }
	
	/**
	 *  Called when the result is available.
	 * @param result The result.
	 */
	public abstract void customResultAvailable(Collection<E> result);
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
