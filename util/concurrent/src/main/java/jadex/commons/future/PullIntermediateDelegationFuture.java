package jadex.commons.future;

/**
 *  Delegation future for pull future.
 */
public class PullIntermediateDelegationFuture<E> extends TerminableIntermediateDelegationFuture<E>
	implements IPullIntermediateFuture<E>
{
	//-------- constructors --------
	
	/**
	 *  Create a new future.
	 */
	public PullIntermediateDelegationFuture()
	{
	}
	
	/**
	 *  Create a new future.
	 */
	public PullIntermediateDelegationFuture(IPullIntermediateFuture<E> src)
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

