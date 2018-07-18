package jadex.commons.future;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


/**
 * 
 */
public class SubscriptionIntermediateDelegationFuture<E> extends TerminableIntermediateDelegationFuture<E>
	implements ISubscriptionIntermediateFuture<E>
{
	//-------- attributes --------
	
	/** The local results for a single thread. */
    protected Map<Thread, List<E>>	ownresults;
	
    /** Flag if results should be stored till first listener is. */
    protected boolean storeforfirst;
	
	/**
	 *  Create a new future.
	 */
	public SubscriptionIntermediateDelegationFuture()
	{
		storeforfirst = true;
	}
	
	/**
	 *  Create a new future.
	 */
	public SubscriptionIntermediateDelegationFuture(ITerminableIntermediateFuture<?> src)
	{
		super(src);
		storeforfirst = true;
	}
	
	//-------- methods (hack!!! copied from subscription future) --------
	
	/**
	 *  Add a result.
	 *  @param result The result.
	 */
	
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

	Exception e;
	
	/**
     *  Add a result listener.
     *  @param listsner The listener.
     */
    public void	addResultListener(IResultListener<Collection<E>> listener)
    {
    	if(!(listener instanceof IIntermediateResultListener))
    	{
    		throw new IllegalArgumentException("Subscription futures require intermediate listeners.");
    	}
    	
//    	if(storeforfirst)
//    		e = new RuntimeException();
//    	
//    	if(!storeforfirst && listeners!=null && listeners.size()>=0)
//    	{
//    		e.printStackTrace();
//    		System.out.println("adding listener: "+this+" "+listener);
//    	}
    	
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
     *  @return	True, when there are more intermediate results for the caller.
     */
	@Override
    public boolean hasNextIntermediateResult()
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
			
    		ret	= !ownres.isEmpty();
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
    	    		// todo: realtime as method parameter?!
    				caller.suspend(this, UNSET, false);
    	    	   	icallers.remove(caller);
    			}
    			// else already resumed.
    		}
	    	ret	= hasNextIntermediateResult();
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
    		    	ret	= doGetNextIntermediateResult(index, timeout, realtime);
    			}
    			// else already resumed.
    		}
    	}
    	
    	return ret;
    }
}
