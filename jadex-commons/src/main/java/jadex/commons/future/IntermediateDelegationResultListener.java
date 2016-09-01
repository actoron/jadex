package jadex.commons.future;

import java.util.Collection;
import java.util.logging.Logger;


/**
 * Intermediate version of the delegation result listener.
 */
public class IntermediateDelegationResultListener<E> implements IIntermediateResultListener<E>, IFutureCommandListener, IUndoneIntermediateResultListener<E>
{
	// -------- attributes --------

	/** The future to which calls are delegated. */
	protected IntermediateFuture<E>				future;

	/** Flag if undone methods should be used. */
	protected boolean							undone;

	/** Custom functional result listener */
	protected IIntermediateResultListener<E>	delegate;

	// -------- constructors --------

//	public IntermediateDelegationResultListener(final IFunctionalResultListener<E> intermediateListener, final IFunctionalResultListener<Void> finishedListener,
//		final IFunctionalExceptionListener exceptionListener)
//	{
//		this(new IntermediateDefaultResultListener<E>()
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				exceptionListener.exceptionOccurred(exception);
//			}
//
//			public void intermediateResultAvailable(E result)
//			{
//				intermediateListener.resultAvailable(result);
//			}
//
//			public void finished()
//			{
//				finishedListener.resultAvailable(null);
//			}
//		});
//	}

	/**
	 * Create a new listener.
	 * @param delegate The delegation target.
	 */
	public IntermediateDelegationResultListener(IIntermediateResultListener<E> delegate)
	{
		this(delegate, false);
	}

	/**
	 * Create a new listener.
	 * @param delegate The delegation target.
	 * @param undone use undone methods.
	 */
	public IntermediateDelegationResultListener(IIntermediateResultListener<E> delegate, boolean undone)
	{
		this.delegate = delegate;
		this.undone = undone;
	}

	/**
	 * Create a new listener.
	 * @param future The delegation target.
	 */
	public IntermediateDelegationResultListener(IntermediateFuture<E> future)
	{
		this(future, false);
	}

	/**
	 * Create a new listener.
	 * @param future The delegation target.
	 * @param undone use undone methods.
	 */
	public IntermediateDelegationResultListener(IntermediateFuture<E> future, boolean undone)
	{
		this.future = future;
		this.undone = undone;
	}

	// -------- methods --------

	/**
	 * Called when the result is available.
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
			if(e.getFuture() == future)
			{
				// Rethrow duplicate result exception to notify about usage
				// error.
				throw e;
			}
			else
			{
//				future.setExceptionIfUndone(e);
				handleException(e);
			}
		}
		catch(Exception e)
		{
			// e.printStackTrace();
			// Could happen that overridden customResultAvailable method
			// first sets result and then throws exception (listener ex are
			// catched).
			//future.setExceptionIfUndone(e);
			handleException(e);
		}
	}

	/**
	 * Called when an intermediate result is available.
	 * @param result The result.
	 */
	public final void intermediateResultAvailable(E result)
	{
		try
		{
			customIntermediateResultAvailable(result);
		}
		catch(DuplicateResultException e)
		{
			if(e.getFuture() == future)
			{
				// Rethrow duplicate result exception to notify about usage
				// error.
				throw e;
			}
			else
			{
//				future.setExceptionIfUndone(e);
				handleException(e);
			}
		}
		catch(Exception e)
		{
			// e.printStackTrace();
			// Could happen that overridden customResultAvailable method
			// first sets result and then throws exception (listener ex are
			// catched).
//			future.setExceptionIfUndone(e);
			handleException(e);
		}
	}

	/**
	 * Declare that the future is finished.
	 */
	public void finished()
	{
		if(delegate != null)
		{
			if(undone && delegate instanceof IUndoneIntermediateResultListener)
			{
				((IUndoneIntermediateResultListener)delegate).finishedIfUndone();
			}
			else
			{
				delegate.finished();
			}
		}
		else
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
	}

	/**
	 * Called when the result is available.
	 * @param result The result.
	 */
	public void customResultAvailable(Collection<E> result)
	{
		if(delegate != null)
		{
			if(undone && delegate instanceof IUndoneResultListener)
			{
				((IUndoneResultListener)delegate).resultAvailableIfUndone(result);
			}
			else
			{
				delegate.resultAvailable(result);
			}
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
	 * Called when an exception occurred.
	 * @param exception The exception.
	 */
	public void exceptionOccurred(Exception exception)
	{
		if(delegate != null)
		{
			if(undone && delegate instanceof IUndoneResultListener)
			{
				((IUndoneResultListener)delegate).exceptionOccurredIfUndone(exception);
			}
			else
			{
				delegate.exceptionOccurred(exception);
			}
		}
		else
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
	}

	/**
	 * Called when an intermediate result is available.
	 * @param result The result.
	 */
	public void customIntermediateResultAvailable(E result)
	{
		if(delegate != null)
		{
			if(undone && delegate instanceof IUndoneIntermediateResultListener)
			{
				((IUndoneIntermediateResultListener)delegate).intermediateResultAvailableIfUndone(result);
			}
			else
			{
				delegate.intermediateResultAvailable(result);
			}
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
	 * Called when a command is available.
	 */
	public void commandAvailable(Object command)
	{
		if(delegate instanceof IFutureCommandListener)
		{
			((IFutureCommandListener)delegate).commandAvailable(command);
		}
		if(future instanceof IForwardCommandFuture)
		{
			((IForwardCommandFuture)future).sendForwardCommand(command);
		}
		else
		{
			// System.out.println("Cannot forward command: "+future+"
			// "+command);
			Logger.getLogger("intermediate-delegation-result-listener").fine("Cannot forward command: " + future + " " + command);
		}
	}

	/**
	 * Called when the result is available.
	 * 
	 * @param result The result.
	 */
	public void resultAvailableIfUndone(Collection<E> result)
	{
		undone = true;
		resultAvailable(result);
	}

	/**
	 * Called when an exception occurred.
	 * 
	 * @param exception The exception.
	 */
	public void exceptionOccurredIfUndone(Exception exception)
	{
		undone = true;
		exceptionOccurred(exception);
	}

	/**
	 * Called when an intermediate result is available.
	 * 
	 * @param result The result.
	 */
	public void intermediateResultAvailableIfUndone(E result)
	{
		undone = true;
		intermediateResultAvailable(result);
	}

	/**
	 * Declare that the future is finished. This method is only called for
	 * intermediate futures, i.e. when this method is called it is guaranteed
	 * that the intermediateResultAvailable method was called for all
	 * intermediate results before.
	 */
	public void finishedIfUndone()
	{
		undone = true;
		finished();
	}
	
	/**
	 *  Handle an exception.
	 */
	protected void handleException(Exception e)
	{
		if(future!=null)
		{
			// e.printStackTrace();
			// Could happen that overridden customResultAvailable method
			// first sets result and then throws exception (listener ex are
			// catched).
			future.setExceptionIfUndone(e);
		}
		else
		{
			if(undone && delegate instanceof IUndoneResultListener)
			{
				((IUndoneResultListener)delegate).exceptionOccurredIfUndone(e);
			}
			else
			{
				delegate.exceptionOccurred(e);
			}
		}
	}
}
