package jadex.commons.future;

import jadex.commons.future.ICommandFuture.Type;

import java.util.Collection;
import java.util.logging.Logger;

/**
 * 
 */
public abstract class IntermediateExceptionDelegationResultListener<E, T> implements IIntermediateResultListener<E>, IFutureCommandListener
{
	//-------- attributes --------
	
	/** The future to which calls are delegated. */
	protected Future<T> future;
	
//	protected DebugException	ex;
	
	//-------- constructors --------
	
	/**
	 *  Create a new listener.
	 */
	public IntermediateExceptionDelegationResultListener(Future<T> future)
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
    public abstract void finished();
	
	/**
	 *  Called when the result is available.
	 * @param result The result.
	 */
	public void customResultAvailable(Collection<E> result)
	{
		for(E e: result)
		{
			intermediateResultAvailable(e);
		}
		finished();
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
	
	/**
	 *  Called when a command is available.
	 */
	public void commandAvailable(Type command)
	{
		if(future instanceof ICommandFuture)
		{
			((ICommandFuture)future).sendCommand(command);
		}
		else
		{
//			System.out.println("Cannot forward command: "+future+" "+command);
			Logger.getLogger("intermediate-exception-delegation-result-listener").warning("Cannot forward command: "+future+" "+command);
		}
	}
}
