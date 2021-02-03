package jadex.commons.future;

import java.util.Collection;
import java.util.logging.Logger;

import jadex.commons.SUtil;


/**
 * Intermediate version of the delegation result listener.
 */
public class IntermediateDelegationResultListener<E> implements IIntermediateResultListener<E>, IFutureCommandResultListener<Collection<E>>, IUndoneIntermediateResultListener<E>
{
	// -------- attributes --------

	/** The future to which calls are delegated. */
	protected IntermediateFuture<E>	future;

	/** Flag if undone methods should be used. */
	protected boolean undone;

	/** Custom functional result listener */
	protected IIntermediateResultListener<E> delegate;

	/** Custom functional result listener */
	protected IFunctionalResultListener<Collection<E>> crlistener;

	/** Custom functional intermediate result listener */
	protected IFunctionalIntermediateResultListener<E> cirlistener;
	
	/** Custom functional result count listener. */
	protected IFunctionalIntermediateResultCountListener clistener;

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
		this(future, null, null, null);
	}

	/**
	 * Create a new listener.
	 * @param future The delegation target.
	 * @param crlistener Custom result listener that overwrites the delegation behaviour.
	 * @param cirlistener Custom intermediate result listener that overwrites the delegation behaviour.
	 */
	public IntermediateDelegationResultListener(IntermediateFuture<E> future, IFunctionalResultListener<Collection<E>> crlistener, IFunctionalIntermediateResultListener<E> cirlistener, IFunctionalIntermediateResultCountListener clistener)
	{
		this(future, false, crlistener, cirlistener, clistener);
	}

	/**
	 * Create a new listener.
	 * @param future The delegation target.
	 * @param undone use undone methods.
	 */
	public IntermediateDelegationResultListener(IntermediateFuture<E> future, boolean undone) 
	{
		this(future, undone, null, null, null);
	}
	
	/**
	 * Create a new listener.
	 * @param future The delegation target.
	 * @param undone use undone methods.
	 * @param crlistener Custom result listener that overwrites the delegation behaviour.
	 * @param cirlistener Custom intermediate result listener that overwrites the delegation behaviour.
	 */
	public IntermediateDelegationResultListener(IntermediateFuture<E> future, boolean undone, IFunctionalResultListener<Collection<E>> crlistener, IFunctionalIntermediateResultListener<E> cirlistener, IFunctionalIntermediateResultCountListener clistener)
	{
		this.future = future;
		this.undone = undone;
		this.crlistener = crlistener;
		this.cirlistener = cirlistener;
		this.clistener = clistener;
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
//		//-------- debugging --------
//		if(future!=null && (""+future.results).contains("PartDataChunk"))
//		{
//			System.out.println("IntermediateDelegationResultListener.finished: "+future+", "+future.listeners+", "+ this+", "+Thread.currentThread()
//				+"\n"+SUtil.getExceptionStacktrace(new Exception("Stack trace").fillInStackTrace()));
//		}
//		//-------- debugging end --------

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
		if(crlistener != null)
		{
			crlistener.resultAvailable(result);
		}
		else
		{
			if (delegate != null) 
			{
				if (undone && delegate instanceof IUndoneResultListener) 
				{
					((IUndoneResultListener) delegate).resultAvailableIfUndone(result);
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
//		//-------- debugging --------
//		if(future!=null && (""+result).contains("PartDataChunk"))
//		{
//			System.out.println("IntermediateDelegationResultListener.customIntermediateResultAvailable: "+future+", "+result+", "+future.listeners+", "+ this+", "+Thread.currentThread()
//				+"\n"+SUtil.getExceptionStacktrace(new Exception("Stack trace").fillInStackTrace()));
//		}
//		//-------- debugging end --------

		if(cirlistener != null)
		{
			cirlistener.intermediateResultAvailable(result);
		}
		else
		{
			if(delegate != null) 
			{
				if (undone && delegate instanceof IUndoneIntermediateResultListener) 
				{
					((IUndoneIntermediateResultListener) delegate).intermediateResultAvailableIfUndone(result);
				} 
				else 
				{
					delegate.intermediateResultAvailable(result);
				}
			} 
			else 
			{
				if (undone) 
				{
					future.addIntermediateResultIfUndone(result);
				} 
				else 
				{
					future.addIntermediateResult(result);
				}
			}
		}
	}
	
	/**
	 *  Declare that the future result count is available.
	 *  This method is only called for intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method will be called as
	 *  often as the result count indicates.
	 */
	public void maxResultCountAvailable(int max) 
	{
		if(clistener != null)
		{
			clistener.maxResultCountAvailable(max);
		}
		else
		{
			if(delegate != null) 
			{
//				if(undone && delegate instanceof IUndoneIntermediateResultListener) 
//				{
//					((IUndoneIntermediateResultListener)delegate).res
//				} 
//				else 
//				{
					delegate.maxResultCountAvailable(max);
				//}
			} 
			else 
			{
				//if(undone) 
				//{
				//	future.setMaxResultCount(count);
				//} 
				//else 
				//{
					future.setMaxResultCount(max);
				//}
			}
		}
	}

	/**
	 * Called when a command is available.
	 */
	public void commandAvailable(Object command)
	{
		if(delegate instanceof IFutureCommandResultListener)
		{
			((IFutureCommandResultListener<?>)delegate).commandAvailable(command);
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
