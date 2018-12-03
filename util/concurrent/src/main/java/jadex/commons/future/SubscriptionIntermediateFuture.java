package jadex.commons.future;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import jadex.commons.SUtil;


/**
 *  Implementation of the subscription intermediate future.
 */
public class SubscriptionIntermediateFuture<E> extends TerminableIntermediateFuture<E>
	implements ISubscriptionIntermediateFuture<E>
{    
	//-------- attributes --------
	
	/** The local results for a single thread. */
	// Not thread local to fetch all entries in add result.
    protected Map<Thread, List<E>>	ownresults;
	
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
		this(terminate, true);
	}
	
	/**
	 *  Create a new future.
	 *  @param terminate The code to be executed in case of termination.
	 */
	public SubscriptionIntermediateFuture(ITerminationCommand terminate, boolean storeforfirst)
	{
		super(terminate);
		this.storeforfirst = storeforfirst;
	}
	
	//-------- methods --------
	
	/**
	 *  Store a result.
	 *  @param result The result.
	 */
	@Override
	protected void storeResult(E result)
	{
		// Store results only if necessary for first listener.
		if(storeforfirst)
			super.storeResult(result);
		
		if(ownresults!=null)
		{
			for(List<E> res: ownresults.values())
			{
				res.add(result);
			}
		}
		
		resumeIntermediate();
	}
	
	/**
	 *  Add a listener which is only informed about new results,
	 *  i.e. the initial results are not posted to this listener,
	 *  even if it is the first listener to be added to this future.
	 */
	public void	addQuietListener(IResultListener<Collection<E>> listener)
	{
    	if(!(listener instanceof IIntermediateResultListener))
    	{
    		throw new IllegalArgumentException("Subscription futures require intermediate listeners.");
    	}
    	
    	super.addResultListener(listener);		
	}

	
	/**
     *  Add a result listener.
     *  @param listsner The listener.
     */
	@Override
    public void	addResultListener(IResultListener<Collection<E>> listener)
    {
    	if(!(listener instanceof IIntermediateResultListener))
    		throw new IllegalArgumentException("Subscription futures require intermediate listeners.");
    	
//    	System.out.println("adding listener: "+this+" "+listener);
    	
    	boolean first;
    	synchronized(this)
		{
			first = storeforfirst;
			storeforfirst = false;
		}
    	super.addResultListener(listener);
    	
		if(first)
		{
			results = null;
		}
    }
	
    /**
     *  Get the intermediate results that are available.
     *  Note: The semantics of this method is different to the normal intermediate future
     *  due to the fire-and-forget-semantics!
     *  
     *  @return
     *  1) <i>Non-blocking</I> access only: An empty collection, unless if the future is in "store-for-first" mode (default)
     *  	and no listeners has yet been added, in which case the results until now are returned.<br>
     *  2) Also <i>blocking</i> access from same thread: All results since the first blocking access
     *  	that have not yet been consumed by getNextIntermediateResult().
     */
	public Collection<E> getIntermediateResults()
	{
		List<E>	ret;

    	synchronized(this)
    	{
			if(storeforfirst)
			{
				ret	= results;
			}
			else
			{
	    		ret	= ownresults!=null ? ownresults.get(Thread.currentThread()) : null;
			}
    	}

    	return ret!=null ? ret : Collections.emptyList();
	}
	
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
	@Override
    public E getNextIntermediateResult(long timeout, boolean realtime)
    {
    	return doGetNextIntermediateResult(0, timeout, realtime);
    }

    /**
     *  Check if there are more results for iteration for the given caller.
     *  If there are currently no unprocessed results and future is not yet finished,
     *  the caller is blocked until either new results are available and true is returned
     *  or the future is finished, thus returning false.
     *  
	 *  @param timeout The timeout in millis.
	 *  @param realtime Flag, if wait should be realtime (in constrast to simulation time).
     *  @return	True, when there are more intermediate results for the caller.
     */
	@Override
    public boolean hasNextIntermediateResult(long timeout, boolean realtime)
    {
    	boolean	ret;
    	boolean	suspend;
		ISuspendable caller = ISuspendable.SUSPENDABLE.get();
	   	if(caller==null)
	   	{
	   		caller = new ThreadSuspendable();
	   	}

		List<E>	ownres;

    	synchronized(this)
    	{
			if(storeforfirst)
			{
				storeforfirst	= false;
				ownres	= results;
				results	= null;
			}
			else
			{
	    		ownres	= ownresults!=null ? ownresults.get(Thread.currentThread()) : null;
			}

			if(ownres==null)
			{
    			ownres	= new LinkedList<E>();
			}
			
			if(ownresults==null || !ownresults.containsKey(Thread.currentThread()))
			{
				ownresults	= ownresults!=null ? ownresults : new HashMap<Thread, List<E>>();
				ownresults.put(Thread.currentThread(), ownres);
			}
			
    		ret	= !ownres.isEmpty() || isDone() && getException()!=null;
    		suspend	= !ret && !isDone();
    		if(suspend)
    		{
	    	   	if(icallers==null)
	    	   	{
	    	   		icallers	= Collections.synchronizedMap(new HashMap<ISuspendable, String>());
	    	   	}
	    	   	icallers.put(caller, CALLER_QUEUED);
    		}
    	}
    	
    	if(suspend)
    	{
	    	Object mon = caller.getMonitor()!=null? caller.getMonitor(): caller;
	    	synchronized(mon)
	    	{
    			Object	state	= icallers.get(caller);
    			if(CALLER_QUEUED.equals(state))
    			{
    	    	   	icallers.put(caller, CALLER_SUSPENDED);
    				caller.suspend(this, timeout, realtime);
    	    	   	icallers.remove(caller);
    			}
    			// else already resumed.
    		}
	    	ret	= hasNextIntermediateResult(timeout, realtime);
    	}
    	
    	return ret;
    }
	
    /**
     *  Perform the get without increasing the index.
     */
    @Override
    protected E doGetNextIntermediateResult(int index, long timeout, boolean realtime)
    {
       	E	ret	= null;
    	boolean	suspend	= false;
		ISuspendable caller = ISuspendable.SUSPENDABLE.get();
	   	if(caller==null)
	   	{
	   		caller = new ThreadSuspendable();
	   	}

		List<E>	ownres;

    	synchronized(this)
    	{
			if(storeforfirst)
			{
				storeforfirst	= false;
				ownres	= results;
				results	= null;
			}
			else
			{
	    		ownres	= ownresults!=null ? ownresults.get(Thread.currentThread()) : null;
			}

			if(ownres==null)
			{
    			ownres	= new LinkedList<E>();
			}
			
			if(ownresults==null || !ownresults.containsKey(Thread.currentThread()))
			{
				ownresults	= ownresults!=null ? ownresults : new HashMap<Thread, List<E>>();
				ownresults.put(Thread.currentThread(), ownres);
			}
			
    		if(!ownres.isEmpty())
    		{
    			ret	= ownres.remove(0);
    		}
    		else if(isDone())
    		{
    			if(getException()!=null)
    				throw SUtil.throwUnchecked(getException());
    			else
    				throw new NoSuchElementException("No more intermediate results.");
    		}
    		else
    		{
    			suspend	= true;
	    	   	if(icallers==null)
	    	   	{
	    	   		icallers	= Collections.synchronizedMap(new HashMap<ISuspendable, String>());
	    	   	}
	    	   	icallers.put(caller, CALLER_QUEUED);
    		}
    	}
    	
    	if(suspend)
    	{
	    	Object mon = caller.getMonitor()!=null? caller.getMonitor(): caller;
	    	synchronized(mon)
	    	{
    			Object	state	= icallers.get(caller);
    			if(CALLER_QUEUED.equals(state))
    			{
    	    	   	icallers.put(caller, CALLER_SUSPENDED);
    				caller.suspend(this, timeout, realtime);
    	    	   	icallers.remove(caller);
    			}
    			// else already resumed.
	    	}
	    	
	    	// Re-call outside synchronized!
    		ret	= doGetNextIntermediateResult(index, timeout, realtime);
    	}
    	
    	return ret;
    }
}
