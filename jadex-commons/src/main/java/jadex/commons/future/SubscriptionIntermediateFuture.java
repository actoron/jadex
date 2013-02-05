package jadex.commons.future;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;


/**
 *  Implementation of the subscription intermediate future.
 */
public class SubscriptionIntermediateFuture<E> extends TerminableIntermediateFuture<E>
	implements ISubscriptionIntermediateFuture<E>
{
	//-------- constants --------
	
	/** The local results for a single thread. */
    protected static final	ThreadLocal<List<?>>	OWNRESULTS	= new ThreadLocal<List<?>>();
    
	//-------- attributes --------
	
    /** Flag if results should be stored till first listener is added. */
    protected boolean storeforfirst;
	
	//-------- constructors --------

	/**
	 *  Create a new future.
	 */
	public SubscriptionIntermediateFuture()
	{
		this((ITerminationCommand)null);
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
	 *  @param terminate The code to be executed in case of termination.
	 */
	public SubscriptionIntermediateFuture(ITerminationCommand terminate)
	{
		super(terminate);
		this.storeforfirst = true;
	}
	
	//-------- methods --------
	
	/**
	 *  Add a result.
	 *  @param result The result.
	 */
	protected void addResult(E result)
	{
		// Store results only if necessary for first listener.
		if(storeforfirst)
			super.addResult(result);
	}
	
	/**
     *  Add a result listener.
     *  @param listsner The listener.
     */
    public void	addResultListener(IResultListener<Collection<E>> listener)
    {
//    	System.out.println("adding listener: "+listener);
    	boolean first;
    	synchronized(this)
		{
			first = storeforfirst;
			storeforfirst	= false;
		}
    	super.addResultListener(listener);
    	
		if(first)
		{
			results=null;
		}
    }
    
    /**
     *  Check if there are more results for iteration for the given caller.
     *  If there are currently no unprocessed results and future is not yet finished,
     *  the caller is blocked until either new results are available and true is returned
     *  or the future is finished, thus returning false.
     *  
     *  @return	True, when there are more intermediate results for the caller.
     */
//    public boolean hasNextIntermediateResult()
//    {
//    	boolean	ret;
//    	boolean	suspend;
//    	
//		Integer	index	= INDICES.get();
//		List<E>	ownresults	= OWNRESULTS.get();
//		if(index==null)
//		{
//			index	= new Integer(0);
//		}
//		
//		ISuspendable	caller	= null;
//    	synchronized(this)
//    	{
//    		ret	= results!=null && results.size()>index.intValue();
//    		suspend	= !ret && !isDone();
//    		if(suspend)
//    		{
//    	    	caller	= ISuspendable.SUSPENDABLE.get();
//    	    	if(caller==null)
//    	    	{
//    		   		throw new RuntimeException("No suspendable element.");
//    	    	}
//	    	   	if(icallers==null)
//	    	   	{
//	    	   		icallers	= Collections.synchronizedMap(new HashMap<ISuspendable, String>());
//	    	   	}
//	    	   	icallers.put(caller, CALLER_QUEUED);
//    		}
//    	}
//    	
//    	if(suspend)
//    	{
//	    	Object mon = caller.getMonitor()!=null? caller.getMonitor(): caller;
//	    	synchronized(mon)
//	    	{
//    			Object	state	= icallers.get(caller);
//    			if(CALLER_QUEUED.equals(state))
//    			{
//    	    	   	icallers.put(caller, CALLER_SUSPENDED);
//    				caller.suspend(this, -1);
//    	    	   	icallers.remove(caller);
//    		    	ret	= hasNextIntermediateResult();
//    			}
//    			// else already resumed.
//    		}
//    	}
//    	
//    	return ret;
//    }	
	
    /**
     *  Iterate over the intermediate results in a blocking fashion.
     *  Manages results independently for different callers, i.e. when called
     *  from different threads, each thread receives all intermediate results.
     *  
     *  The operation is guaranteed to be non-blocking, if hasNextIntermediateResult()
     *  has returned true before for the same caller. Otherwise the caller is blocked
     *  until a result is available or the future is finished.
     *  
     *  @return	The next intermediate result.
     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
     */
//    public E getNextIntermediateResult()
//    {
//		Integer	index	= INDICES.get();
//		index	= index==null ? new Integer(1) : new Integer(index.intValue()+1);
//		INDICES.set(index);
//		return doGetNextIntermediateResult(index.intValue());
//    }
    
}
