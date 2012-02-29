package jadex.commons.future;

/**
 *  Interface for a future that can be terminated
 *  from caller side. A termination request leads
 *  to setException() being called with a 
 *  FutureTerminatedException.
 */
public interface ITerminableFuture<E> extends IFuture<E>
{
	/**
	 *  Terminate the future.
	 */
	public void terminate();
}
