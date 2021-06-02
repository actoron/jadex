package jadex.commons.future;

import jadex.commons.ICommand;



/**
 *  Intermediate future with pull mechanism.
 *  Allows for pulling results by the caller.
 *  In this way a pull intermediate future is 
 *  similar to an iterator.
 */
public class PullIntermediateFuture<E> extends TerminableIntermediateFuture<E> 
	implements IPullIntermediateFuture<E>
{
	//-------- attributes --------
	
	/** The pull command. */
	protected ICommand<PullIntermediateFuture<E>> pullcmd;
	
	//-------- constructors --------

	/**
	 *  Create a new future.
	 */
	public PullIntermediateFuture()
	{
	}
	
	/**
	 *  Create a future that is already done (failed).
	 *  @param exception	The exception.
	 */
	public PullIntermediateFuture(Exception exception)
	{
		super(exception);
	}
	
	/**
	 *  Create a new future.
	 *  @param terminate The runnable to be executed in case of termination.
	 */
	public PullIntermediateFuture(ICommand<PullIntermediateFuture<E>> pullcmd)
	{
		this.pullcmd = pullcmd;
	}
	
	/**
	 *  Create a new future.
	 *  @param terminate The runnable to be executed in case of termination.
	 */
	public PullIntermediateFuture(ICommand<PullIntermediateFuture<E>> pullcmd,
		ITerminationCommand termcom)
	{
		super(termcom);
		this.pullcmd = pullcmd;
	}
	
	//-------- methods --------

	/**
	 *  Pull an intermediate result.
	 */
	public void pullIntermediateResult()
	{
		if(isDone())
			throw new IllegalStateException("Cannot pull new intermediate results when future already finished");
		pullcmd.execute(this);
	}
}

