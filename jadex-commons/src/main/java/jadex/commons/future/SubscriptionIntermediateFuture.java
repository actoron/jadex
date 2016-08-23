package jadex.commons.future;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


/**
 *  Implementation of the subscription intermediate future.
 */
public class SubscriptionIntermediateFuture<E> extends TerminableIntermediateFuture<E>
	implements ISubscriptionIntermediateFuture<E>
{    
	//-------- attributes --------
	
	/** The local results for a single thread. */
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
    public void	addResultListener(IResultListener<Collection<E>> listener)
    {
    	if(!(listener instanceof IIntermediateResultListener))
    	{
    		throw new IllegalArgumentException("Subscription futures require intermediate listeners.");
    	}
    	
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
     *  Check if there are more results for iteration for the given caller.
     *  If there are currently no unprocessed results and future is not yet finished,
     *  the caller is blocked until either new results are available and true is returned
     *  or the future is finished, thus returning false.
     *  
     *  @return	True, when there are more intermediate results for the caller.
     */
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
    	boolean first;

    	synchronized(this)
    	{
			first = storeforfirst;
			storeforfirst	= false;
    		
    		Integer	index	= indices!=null ? indices.get(Thread.currentThread()) : null;
    		if(index==null)
    		{
    			index	= Integer.valueOf(0);
    		}
    		ownres	= ownresults!=null ? ownresults.get(Thread.currentThread()) : null;
    		
    		ret	= results!=null && results.size()>index.intValue()
    			|| ownres!=null && !ownres.isEmpty();
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
    	
		if(first)
		{
			results=null;
		}
    	
    	if(suspend)
    	{
    		synchronized(this)
    		{
    			if(ownres==null)
    			{
	    			ownres	= new LinkedList<E>();
	    			if(ownresults==null)
	    			{
	    				ownresults	= new HashMap<Thread, List<E>>();
	    			}
	    			ownresults.put(Thread.currentThread(), ownres);
    			}
    		}
    		
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
    protected E doGetNextIntermediateResult(int index)
    {
       	E	ret	= null;
    	boolean	suspend	= false;
		ISuspendable caller = ISuspendable.SUSPENDABLE.get();
	   	if(caller==null)
	   	{
	   		caller = new ThreadSuspendable();
	   	}

    	List<E>	ownres;
    	boolean first;

    	synchronized(this)
    	{
			first = storeforfirst;
			storeforfirst	= false;

			ownres	= ownresults!=null ? ownresults.get(Thread.currentThread()) : null;
    		if(ownres!=null && !ownres.isEmpty())
    		{
    			ret	= ownres.remove(0);
    		}
    		else if(results!=null && results.size()>index)
    		{
    			// Hack!!! it there a better way to access the i-est element?
    			Iterator<E>	it	= results.iterator();
    			for(int i=0; i<=index; i++)
    			{
    				ret	= it.next();
    			}
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
    	
		if(first)
		{
			results=null;
		}
    	
    	if(suspend)
    	{
    		synchronized(this)
    		{
    			if(ownres==null)
    			{
	    			ownres	= new LinkedList<E>();
	    			if(ownresults==null)
	    			{
	    				ownresults	= new HashMap<Thread, List<E>>();
	    			}
	    			ownresults.put(Thread.currentThread(), ownres);
    			}
    		}

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
	    	
	    	ret	= doGetNextIntermediateResult(index);
    		synchronized(this)
    		{
    			ownresults.remove(Thread.currentThread());
    		}
    	}
    	
    	return ret;
    }	
}
