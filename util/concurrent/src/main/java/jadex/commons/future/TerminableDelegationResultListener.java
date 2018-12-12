package jadex.commons.future;

/**
 *  Result listener for terminable futures. 
 *  Requires the source and target as parameters
 *  and establishes their connection.
 */
public class TerminableDelegationResultListener<E> extends DelegationResultListener<E>
{
	/**
	 *  Create a new listener.
	 *  @param future The target future (to which is delegated).
	 *  @param src The source future which is monitored.
	 */
	public TerminableDelegationResultListener(TerminableDelegationFuture<E> future, ITerminableFuture<E> src)
	{
		super(future);
		future.setTerminationSource(src);
	}
}
