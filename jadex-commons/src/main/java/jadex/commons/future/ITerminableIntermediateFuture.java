package jadex.commons.future;

/**
 *  Interface for an intermediate future that can be terminated
 *  from caller side. A termination request leads
 *  to setException() being called with a FutureTerminatedException.
 */
public interface ITerminableIntermediateFuture<E> extends IIntermediateFuture<E>
{
	/**
	 *  Terminate the future.
	 */
	public void terminate();
}
