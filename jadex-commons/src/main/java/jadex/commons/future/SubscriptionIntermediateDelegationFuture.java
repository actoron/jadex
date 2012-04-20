package jadex.commons.future;


/**
 * 
 */
public class SubscriptionIntermediateDelegationFuture<E> extends TerminableIntermediateDelegationFuture<E>
	implements ISubscriptionIntermediateFuture<E>
{
	/**
	 *  Create a new future.
	 */
	public SubscriptionIntermediateDelegationFuture()
	{
		super();
	}
	
	/**
	 *  Create a new future.
	 */
	public SubscriptionIntermediateDelegationFuture(ITerminableIntermediateFuture<?> src)
	{
		super(src);
	}
}
