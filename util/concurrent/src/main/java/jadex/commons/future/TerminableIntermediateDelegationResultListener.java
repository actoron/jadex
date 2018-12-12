package jadex.commons.future;

/**
 *  Result listener for terminable intermediate futures. 
 *  Requires the source and target as parameters
 *  and establishes their connection.
 */
public class TerminableIntermediateDelegationResultListener<E> extends IntermediateDelegationResultListener<E>
{
	/**
	 *  Create a new listener.
	 *  @param future The target future (to which is delegated).
	 *  @param src The source future which is monitored.
	 */
	public TerminableIntermediateDelegationResultListener(TerminableIntermediateDelegationFuture<E> future, 
		ITerminableIntermediateFuture<E> src)
	{
		super(future);
		future.setSource(src);
	}
}
