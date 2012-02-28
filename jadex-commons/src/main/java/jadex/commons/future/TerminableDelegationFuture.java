package jadex.commons.future;

/**
 * 
 */
public class TerminableDelegationFuture<E> extends Future<E> implements ITerminableFuture<E>
{
	/** The termination source. */
	protected ITerminableFuture<?> src;
	
	/** Flag if source has to be notified. */
	protected boolean notify;
	
	/** Flag if source has been notified. */
	protected boolean notified;

	
	/**
	 *  Create a new future.
	 */
	public TerminableDelegationFuture()
	{
//		System.out.println("tfut: "+hashCode());
	}
	
	/**
	 *  Create a new future.
	 */
	public TerminableDelegationFuture(ITerminableFuture<?> src)
	{
		src.addResultListener(new TerminableDelegationResultListener(this, src));
	}
	
	/**
	 *  Set the termination source.
	 */
	public void setTerminationSource(ITerminableFuture<?> src)
	{
		assert this.src==null;
		
		boolean mynotify;
		synchronized(this)
		{
			// Notify when someone has called terminate (notify is set)
			// src is set and not already notified
			this.src = src;
			mynotify = notify && !notified;
			notified = notified || mynotify;
		}
		
		if(mynotify)
			src.terminate();
	}
	
	/**
	 *  Terminate the future.
	 */
	public void terminate()
	{
		boolean mynotify;
		synchronized(this)
		{
			// Notify when src is set and not already notified
			// Remember to notify when src is not set and not already notified
			mynotify = src!=null && !notified;
			notify = src==null && !notified;
			notified = notified || mynotify;
		}
		
		if(mynotify)
			src.terminate();
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

