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
	protected ITerminationCommand terminate;
		
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
	public TerminableIntermediateFuture(ITerminationCommand terminate)
	{
		this.terminate = terminate;
	}
	
	//-------- methods --------

	/**
	 *  Terminate the future.
	 *  The exception will be set to FutureTerminatedException.
	 */
	public void terminate()
	{
		terminate(new FutureTerminatedException());
	}
	
	/**
	 *  Terminate the future and supply a custom reason.
	 */
	public void terminate(Exception reason)
	{
		boolean	term = terminate==null || terminate.checkTermination(reason);
		
		if(term && setExceptionIfUndone(reason))
		{
			if(terminate!=null)
				terminate.terminated(reason);
		}
	}
	
	/**
	 *  Get the terminate.
	 *  @return The terminate.
	 */
	public ITerminationCommand getTerminationCommand()
	{
		return terminate;
	}

	/**
	 *  Set the terminate.
	 *  @param terminate The terminate to set.
	 */
	public void setTerminationCommand(ITerminationCommand terminate)
	{
		this.terminate = terminate;
	}
	
//	/**
//	 *  Test if future is terminated.
//	 *  @return True, if terminated.
//	 */
//	public boolean isTerminated()
//	{
//		return isDone() && exception instanceof FutureTerminatedException;
//	}
	
}
