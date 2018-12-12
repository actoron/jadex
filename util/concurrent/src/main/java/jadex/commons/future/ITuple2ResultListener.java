package jadex.commons.future;


/**
 *  Listener for tuple2 futures.
 */
public interface ITuple2ResultListener<E, F> extends IIntermediateResultListener<TupleResult>
{
	/**
	 *  Called when the first result is available.
	 *  @param result The first result.
	 */
	public void firstResultAvailable(E result);
	
	/**
	 *  Called when the first result is available.
	 *  @param result The second result.
	 */
	public void secondResultAvailable(F result);
}
