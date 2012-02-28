package jadex.commons.future;

/**
 * 
 */
public class TerminableDelegationResultListener<E> extends DelegationResultListener<E>
{
	/**
	 *  Create a new listener.
	 */
	public TerminableDelegationResultListener(TerminableDelegationFuture<E> future, ITerminableFuture<E> src)
	{
		super(future);
		future.setTerminationSource(src);
	}
}
