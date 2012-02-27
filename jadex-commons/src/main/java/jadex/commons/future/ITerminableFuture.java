package jadex.commons.future;

/**
 * 
 */
public interface ITerminableFuture<E> extends IFuture<E>
{
	/**
	 *  Terminate the future.
	 */
	public void terminate();
}
