package jadex.commons.future;

/**
 *  Interface for a future that can be terminated
 *  from caller side. A termination request leads
 *  to setException() being called with a 
 *  FutureTerminatedException.
 */
public interface ITerminableFuture<E> extends IFuture<E>, IBackwardCommandFuture
{
	/**
	 *  Terminate the future.
	 *  The exception will be set to FutureTerminatedException.
	 */
	public void terminate();
	
	/**
	 *  Terminate the future and supply a custom reason.
	 */
	public void terminate(Exception reason);
}
