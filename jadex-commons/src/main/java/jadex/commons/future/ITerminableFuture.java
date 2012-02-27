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
	
	/**
	 *  Test if future is terminated.
	 *  @return True, if terminated.
	 */
	public boolean isTerminated();
}
