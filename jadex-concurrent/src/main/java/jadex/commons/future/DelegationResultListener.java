package jadex.commons.future;

import java.util.logging.Logger;



/**
 *  Result listener that delegates calls to a future.
 */
public class DelegationResultListener<E> implements IResultListener<E>, IFutureCommandResultListener<E>, IUndoneResultListener<E>
{
	//-------- attributes --------
	
	/** The future to which calls are delegated. */
	protected Future<E> future;
	
	/** Flag if undone methods should be used. */
	protected boolean undone;

	/** Custom functional result listener */
	protected IFunctionalResultListener<E>	customResultListener;
	
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
	 * Create a new listener.
	 * @param future The delegation target.
	 * @param customResultListener Custom result listener that overwrites the delegation behaviour.
	 */
	public DelegationResultListener(Future<E> future, IFunctionalResultListener<E> customResultListener)
	{
		this(future, false, customResultListener);
	}

	/**
	 * Create a new listener.
	 * @param future The delegation target.
	 * @param undone use undone methods.
	 * @param customResultListener Custom result listener that overwrites the delegation behaviour.
	 */
	public DelegationResultListener(Future<E> future, boolean undone, IFunctionalResultListener<E> customResultListener)
	{
		this(future, undone);
		this.customResultListener = customResultListener;
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
//			e.printStackTrace();
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
	
	/**
	 *  Called when the result is available.
	 * @param result The result.
	 */
	public void customResultAvailable(E result)
	{
		if(customResultListener != null)
		{
			customResultListener.resultAvailable(result);
		}
		else
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
			Logger.getLogger("delegation-result-listener").fine("Cannot forward command: "+future+" "+command);
		}
	}
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void resultAvailableIfUndone(E result)
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
	 *  Get the undone.
	 *  @return The undone.
	 */
	public boolean isUndone()
	{
		return undone;
	}
}
