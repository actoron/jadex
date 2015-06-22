package jadex.commons.future;


import java.util.Collection;
import java.util.logging.Logger;



/**
 *  Intermediate version of the delegation result listener.
 */
public class IntermediateDelegationResultListener<E> implements IIntermediateResultListener<E>, 
	IFutureCommandListener, IUndoneIntermediateResultListener<E>
{
	//-------- attributes --------
	
	/** The future to which calls are delegated. */
	protected IntermediateFuture<E> future;
	
	/** Flag if undone methods should be used. */
	protected boolean undone;

	/** Custom functional result listener */
	protected IFunctionalResultListener<E>	customIntermediateResultListener;
	
	//-------- constructors --------
	

	/**
	 * Create a new listener.
	 * 
	 * @param future The delegation target.
	 * @param undone use undone methods.
	 * @param customResultListener Custom result listener that overwrites the
	 *        delegation behaviour.
	 */
	public IntermediateDelegationResultListener(IntermediateFuture<E> future, IFunctionalResultListener<E> customIntermediateResultListener)
	{
		this(future, false, customIntermediateResultListener);
	}

	/**
	 * Create a new listener.
	 * 
	 * @param future The delegation target.
	 * @param undone use undone methods.
	 * @param customResultListener Custom result listener that overwrites the
	 *        delegation behaviour. Can be null
	 */
	public IntermediateDelegationResultListener(IntermediateFuture<E> future, boolean undone, IFunctionalResultListener<E> customIntermediateResultListener)
	{
		this(future, undone);
		this.customIntermediateResultListener = customIntermediateResultListener;
	}
	
	/**
	 *  Create a new listener.
	 */
	public IntermediateDelegationResultListener(IntermediateFuture<E> future)
	{
		this(future, false);
	}
	
	/**
	 *  Create a new listener.
	 */
	public IntermediateDelegationResultListener(IntermediateFuture<E> future, boolean undone)
	{
		this.future = future;
		this.undone = undone;
	}
	
	//-------- methods --------
	
	/**
	 *  Called when the result is available.
	 * @param result The result.
	 */
	public final void resultAvailable(Collection<E> result)
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
	 *  Called when an intermediate result is available.
	 *  @param result The result.
	 */
	public final void intermediateResultAvailable(E result)
	{
		try
		{
			customIntermediateResultAvailable(result);
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
     *  Declare that the future is finished.
     */
    public void finished()
    {
    	if(undone)
		{
			future.setFinishedIfUndone();
		}
		else
		{
			future.setFinished();
		}
    }
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void customResultAvailable(Collection<E> result)
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
		if(undone)
		{
			future.setExceptionIfUndone(exception);
		}
		else
		{
			future.setException(exception);
		}
	}
	
	/**
	 *  Called when an intermediate result is available.
	 * @param result The result.
	 */
	public void customIntermediateResultAvailable(E result)
	{
		if(customIntermediateResultListener != null)
		{
			customIntermediateResultListener.resultAvailable(result);
		}
		else
		{
			if(undone)
			{
				future.addIntermediateResultIfUndone(result);
			}
			else
			{
				future.addIntermediateResult(result);
			}
		}
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
			Logger.getLogger("intermediate-delegation-result-listener").fine("Cannot forward command: "+future+" "+command);
		}
	}
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void resultAvailableIfUndone(Collection<E> result)
	{
		undone = true;
		resultAvailable(result);
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void exceptionOccurredIfUndone(Exception exception)
	{
		undone = true;
		exceptionOccurred(exception);
	}
	
	/**
	 *  Called when an intermediate result is available.
	 *  @param result The result.
	 */
	public void intermediateResultAvailableIfUndone(E result)
	{
		undone = true;
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
    	undone = true;
    	finished();
    }
}
