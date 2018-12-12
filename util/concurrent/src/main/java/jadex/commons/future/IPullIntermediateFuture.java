package jadex.commons.future;


/**
 *  Intermediate future with pull mechanism.
 *  Allows for pulling results by the caller.
 *  In this way a pull intermediate future is 
 *  similar to an iterator.
 */
public interface IPullIntermediateFuture<E> extends ITerminableIntermediateFuture<E> //IIntermediateFuture<E>
{
	/**
	 *  Pull an intermediate result.
	 */
	public void pullIntermediateResult();
}
