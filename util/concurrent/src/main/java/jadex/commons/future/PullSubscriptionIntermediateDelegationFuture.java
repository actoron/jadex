package jadex.commons.future;


/**
 *  Delegation future for pull future.
 */
public class PullSubscriptionIntermediateDelegationFuture<E> extends SubscriptionIntermediateDelegationFuture<E>
	implements IPullSubscriptionIntermediateFuture<E>, IPullIntermediateFuture<E>
{
	//-------- constructors --------
	
	/**
	 *  Create a new future.
	 */
	public PullSubscriptionIntermediateDelegationFuture()
	{
	}
	
	/**
	 *  Create a new future.
	 */
	public PullSubscriptionIntermediateDelegationFuture(IPullSubscriptionIntermediateFuture<E> src)
	{
		super(src);
	}
	
	//-------- methods --------
		
	/**
	 *  Pull an intermediate result.
	 */
	public void pullIntermediateResult()
	{
		handler.pullIntermediateResult();
	}
}

