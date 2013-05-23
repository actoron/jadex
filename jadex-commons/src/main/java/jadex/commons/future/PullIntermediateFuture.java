package jadex.commons.future;

import jadex.commons.ICommand;



/**
 *  Intermediate future that can be terminated from caller side. 
 *  A termination request leads to setException() being 
 *  called with a FutureTerminatedException.
 *  
 *  The future can be supplied with a command that
 *  gets executed if terminate is called.
 */
public class PullIntermediateFuture<E> extends TerminableIntermediateFuture<E> 
	implements IPullIntermediateFuture<E>
{
	//-------- attributes --------
	
	/** The pull command. */
	protected ICommand<PullIntermediateFuture<E>> pullcmd;
	
	//-------- constructors --------

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
		pullcmd.execute(this);
	}
}

