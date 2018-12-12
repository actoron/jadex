package jadex.commons.future;

import java.util.Iterator;

/**
 *  Helper class for iterating over the results of an intermediate future.
 *  Uses a suspendable for realizing blocking operations.
 */
// Todo: finish implementation in future.
public class IntermediateFutureIterator<E> implements Iterator<E>
{
	//-------- attributes --------
	
	/** The future. */
	protected IIntermediateFuture<E>	fut;
	
	//-------- constructors --------
	
	/**
	 *  Create an intermediate future iterator.
	 */
	public IntermediateFutureIterator(IIntermediateFuture<E> fut)
	{
		this.fut	= fut;
	}
	
	//-------- Iterator interface --------

	/**
	 *  Check for more results.
	 */
	public boolean hasNext()
	{
		return fut.hasNextIntermediateResult();
	}

	/**
	 *  Get next result.
	 */
	public E next()
	{
		return fut.getNextIntermediateResult();
	}
	
	/**
	 *  Not supported.
	 */
	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}
