package jadex.commons.future;

import java.util.Collection;

/**
 * 
 */
public class UnlimitedIntermediateDelegationResultListener<E> implements IIntermediateResultListener<E>
{
	/** The delegate future. */
	protected IntermediateFuture<E> delegate;
	
	public UnlimitedIntermediateDelegationResultListener(IntermediateFuture<E> delegate)
	{
		this.delegate = delegate;
	}
	
	public void intermediateResultAvailable(E result)
	{
		delegate.addIntermediateResultIfUndone(result);
	}

	public void finished()
	{
		// the query is not finished after the status quo is delivered
	}

	public void resultAvailable(Collection<E> results)
	{
		for(E result: results)
		{
			intermediateResultAvailable(result);
		}
		// the query is not finished after the status quo is delivered
	}
	
	public void exceptionOccurred(Exception exception)
	{
		// the query is not finished after the status quo is delivered
	}
}