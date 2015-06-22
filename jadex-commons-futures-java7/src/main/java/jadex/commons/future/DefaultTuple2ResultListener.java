package jadex.commons.future;

import java.util.Collection;

/**
 *  Default tuple2 result listener that implements the 
 *  (obsolete) methods
 *  - resultAvailable
 *  - intermediateResultAvailable
 *  - finished
 */
public abstract class DefaultTuple2ResultListener<E, F> implements ITuple2ResultListener<E, F>
{
	/**
	 *  Called when the result is available.
	 *  This method is only called for non-intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method has not been called.
	 *  @param result The final result.
	 */
	public void resultAvailable(Collection<TupleResult> result)
	{
	}

	/**
	 *  Called when an intermediate result is available.
	 *  @param result The result.
	 */
	public void intermediateResultAvailable(TupleResult result)
	{
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
	}
}
