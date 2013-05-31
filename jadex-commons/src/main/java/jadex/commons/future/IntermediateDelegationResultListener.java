package jadex.commons.future;

import jadex.commons.future.ICommandFuture.Type;

import java.util.Collection;



/**
 *  Intermediate version of the delegation result listener.
 */
public class IntermediateDelegationResultListener<E> implements IIntermediateResultListener<E>, IFutureCommandListener
{
	//-------- attributes --------
	
	/** The future to which calls are delegated. */
	protected IntermediateFuture<E> future;
	
	//-------- constructors --------
	
	/**
	 *  Create a new listener.
	 */
	public IntermediateDelegationResultListener(IntermediateFuture<E> future)
	{
		this.future = future;
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
	public void intermediateResultAvailable(E result)
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
    	future.setFinished();
    }
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void customResultAvailable(Collection<E> result)
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
	public void customIntermediateResultAvailable(E result)
	{
		future.addIntermediateResult(result);
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
			System.out.println("Cannot forward command: "+future+" "+command);
		}
	}
}
