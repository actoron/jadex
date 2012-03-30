package jadex.commons.future;

import java.util.Collection;



/**
 * 
 */
public class SubscriptionIntermediateFuture<E> extends TerminableIntermediateFuture<E>
	implements ISubscriptionIntermediateFuture<E>
{
	//-------- attributes --------
	
    /** Flag if results should be stored till first listener is. */
    protected boolean storeforfirst;
	
	//-------- constructors --------

	/**
	 *  Create a new future.
	 */
	public SubscriptionIntermediateFuture()
	{
		this((Runnable)null);
	}
	
	/**
	 *  Create a future that is already done (failed).
	 *  @param exception	The exception.
	 */
	public SubscriptionIntermediateFuture(Exception exception)
	{
		super(exception);
	}
	
	/**
	 *  Create a new future.
	 *  @param terminate The runnable to be executed in case of termination.
	 */
	public SubscriptionIntermediateFuture(Runnable terminate)
	{
		this.terminate = terminate;
		this.storeforfirst = true;
//		this.intermediate = true;
	}
	
	//-------- methods --------
	
	/**
	 *  Add a result.
	 *  @param result The result.
	 */
	protected void addResult(E result)
	{
		// Store results only if necessary for first listener.
		if(storeforfirst && (listeners==null || listeners.size()==0))
			super.addResult(result);
	}
	
	 /**
     *  Add a result listener.
     *  @param listsner The listener.
     */
    public void	addResultListener(IResultListener<Collection<E>> listener)
    {
    	System.out.println("adding: "+listener);
    	boolean first;
    	synchronized(this)
		{
			first = listeners==null || listeners.size()==0;
		}
    	super.addResultListener(listener);
    	
    	synchronized(this)
		{
			if(first)
				results=null;
		}
    }
}
