package jadex.commons.future;


import java.util.Collection;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 *  Exception delegation listener for intermediate futures.
 */
public class IntermediateExceptionDelegationResultListener<E, T> implements IIntermediateResultListener<E>, 
	IFutureCommandResultListener<Collection<E>>, IUndoneIntermediateResultListener<E>
{
	//-------- attributes --------
	
	/** The future to which calls are delegated. */
	protected Future<T> future;
	
	/** The undone flag. */
	protected boolean undone;
	
	/** Custom functional result listener */
	protected Consumer<E> irlistener;
	
	/** Custom functional finished listener */
	protected Runnable flistener;
	
	/** Custom functional result count listener. */
	protected Consumer<Integer> clistener;
	
//	protected DebugException	ex;
	
	//-------- constructors --------
	
	/**
	 * Create a new listener.
	 * 
	 * @param future The delegation target.
	 * @param irlistener Functional intermediate result
	 *        Listener. Can be <code>null</code>.
	 * @param flistener Functional finished listener. Can be
	 *        <code>null</code>.
	 */
	public IntermediateExceptionDelegationResultListener(Future<T> future, Consumer<E> irlistener, 
		Runnable flistener, Consumer<Integer> clistener)
	{
		this(future);
		this.irlistener = irlistener;
		this.flistener = flistener;
		this.clistener = clistener;
	}
	
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
				if(undone)
				{
					future.setExceptionIfUndone(e);
				}
				else
				{
					future.setException(e);				
				}
			}
		}
		catch(Exception e)
		{
	//		e.printStackTrace();
			// Could happen that overridden customResultAvailable method
			// first sets result and then throws exception (listener ex are catched).
			if(undone)
			{
				future.setExceptionIfUndone(e);
			}
			else
			{
				future.setException(e);				
			}
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
	public void intermediateResultAvailable(E result) 
	{
		if(irlistener != null)
			irlistener.accept(result);
	}
	
	/**
     *  Declare that the future is finished.
	 *  This method is only called for intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method was called for all
	 *  intermediate results before.
     */
    public void finished() 
    {
		if(flistener != null)
			flistener.run();
    }
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void customResultAvailable(Collection<E> result)
	{
		for(E e: result)
			intermediateResultAvailable(e);
		finished();
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
	
	public void maxResultCountAvailable(int max) 
	{
		if(clistener!=null)
			clistener.accept(max);
	}
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void resultAvailableIfUndone(Collection<E> result)
	{
		this.undone = true;
		resultAvailable(result);
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void exceptionOccurredIfUndone(Exception exception)
	{
		this.undone = true;
		exceptionOccurred(exception);
	}
	
	/**
	 *  Called when an intermediate result is available.
	 *  @param result The result.
	 */
	public void intermediateResultAvailableIfUndone(E result)
	{
		this.undone = true;
		intermediateResultAvailable(result);
	}
	
	/**
     *  Declare that the future is finished.
	 *  This method is only called for intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method was called for all
	 *  intermediate results before.
     */
    public void finishedIfUndone()
    {
    	this.undone = true;
    	finished();
    }
	
	/**
	 *  Called when a command is available.
	 */
	public void commandAvailable(Object command)
	{
		if(future instanceof IForwardCommandFuture)
		{
			((IForwardCommandFuture)future).sendForwardCommand(command);
		}
		else
		{
//			System.out.println("Cannot forward command: "+future+" "+command);
			Logger.getLogger("intermediate-exception-delegation-result-listener").fine("Cannot forward command: "+future+" "+command);
		}
	}

	/**
	 *  Get the undone.
	 *  @return The undone.
	 */
	public boolean isUndone()
	{
		return undone;
	}
}
