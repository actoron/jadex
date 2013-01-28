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
	
	/** The suspendable. */
	protected ISuspendable	sus;
	
	//-------- constructors --------
	
	/**
	 *  Create an intermediate future iterator.
	 */
	public IntermediateFutureIterator(IIntermediateFuture<E> fut, ISuspendable sus)
	{
		this.fut	= fut;
		this.sus	= sus;
	}
	
	//-------- Iterator interface --------

	/**
	 *  Check for more results.
	 */
	public boolean hasNext()
	{
//		return fut.hasNextIntermediateResult(sus);
		throw new UnsupportedOperationException();
	}

	/**
	 *  Get next result.
	 */
	public E next()
	{
//		return fut.getNextIntermediateResult(sus);
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Not supported.
	 */
	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}
