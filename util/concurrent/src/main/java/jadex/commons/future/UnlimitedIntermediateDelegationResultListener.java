package jadex.commons.future;

import java.util.Collection;

/**
 *  Listener that forwards results but not finished events.
 */
public class UnlimitedIntermediateDelegationResultListener<E> implements IIntermediateResultListener<E>
{
	/** The delegate future. */
	protected IntermediateFuture<E> delegate;
	
	/**
	 *  Create a new UnlimitedIntermediateDelegationResultListener.
	 */
	public UnlimitedIntermediateDelegationResultListener(IntermediateFuture<E> delegate)
	{
		this.delegate = delegate;
	}
	
	/**
	 *  Called when an intermediate result is available.
	 *  @param result The result.
	 */
	public void intermediateResultAvailable(E result)
	{
		delegate.addIntermediateResultIfUndone(result);
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
		// the query is not finished after the status quo is delivered
	}

	/**
	 *  Called when the result is available.
	 *  This method is only called for non-intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method has not been called.
	 *  @param result The final result.
	 */
	public void resultAvailable(Collection<E> results)
	{
		for(E result: results)
		{
			intermediateResultAvailable(result);
		}
		// the query is not finished after the status quo is delivered
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void exceptionOccurred(Exception exception)
	{
		// the query is not finished after the status quo is delivered
	}
}