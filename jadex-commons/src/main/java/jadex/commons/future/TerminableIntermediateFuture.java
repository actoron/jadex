package jadex.commons.future;

/**
 *  Intermediate future that can be terminated from caller side. 
 *  A termination request leads to setException() being 
 *  called with a FutureTerminatedException.
 *  
 *  The future can be supplied with a command that
 *  gets executed if terminate is called.
 */
public class TerminableIntermediateFuture<E> extends IntermediateFuture<E> 
	implements ITerminableIntermediateFuture<E>
{
	//-------- attributes --------
	
	/** The termination code. */
	protected Runnable terminate;
	
	//-------- constructors --------

	/**
	 *  Create a new future.
	 */
	public TerminableIntermediateFuture()
	{
	}
	
	/**
	 *  Create a future that is already done (failed).
	 *  @param exception	The exception.
	 */
	public TerminableIntermediateFuture(Exception exception)
	{
		super(exception);
	}
	
	/**
	 *  Create a new future.
	 *  @param terminate The runnable to be executed in case of termination.
	 */
	public TerminableIntermediateFuture(Runnable terminate)
	{
		this.terminate = terminate;
	}
	
	//-------- methods --------

	/**
	 *  Terminate the future.
	 */
	public void terminate()
	{
		if(setExceptionIfUndone(new FutureTerminatedException()))
		{
			if(terminate!=null)
				terminate.run();
		}
	}
	
	/**
	 *  Test if future is terminated.
	 *  @return True, if terminated.
	 */
	public boolean isTerminated()
	{
		return isDone() && exception instanceof FutureTerminatedException;
	}
	
}
