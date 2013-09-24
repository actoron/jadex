package jadex.commons.future;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 *  Default implementation of an intermediate future.
 */
public class IntermediateFuture<E> extends Future<Collection <E>> implements IIntermediateFuture<E> 
{
	//-------- attributes --------
	
	/** The intermediate results. */
	protected Collection<E> results;
	
	/** Flag indicating that addIntermediateResult()has been called. */
	protected boolean intermediate;
	
	/** The scheduled notifications. */
	protected List	scheduled;
	
	/** Flag if notifying. */
    protected boolean notifying;
    
	/** The blocked intermediate callers (caller->state). */
	protected Map<ISuspendable, String> icallers;
    
	/** The index of the next result for a thread. */
    protected Map<Thread, Integer>	indices;
           
	//-------- constructors--------
	
	/**
	 *  Create a future that is already done.
	 *  @param result	The result, if any.
	 */
	public IntermediateFuture()
	{
	}
	
	/**
	 *  Create a future that is already done.
	 *  @param result	The result, if any.
	 */
	public IntermediateFuture(Collection<E> results)
	{
		super(results);
	}
	
	/**
	 *  Create a future that is already done (failed).
	 *  @param exception	The exception.
	 */
	public IntermediateFuture(Exception exception)
	{
		super(exception);
	}
	
	//-------- IIntermediateFuture interface --------
		
    /**
     *  Get the intermediate results that are available.
     *  @return The current intermediate results (copy of the list).
     */
	public synchronized Collection<E> getIntermediateResults()
	{
		Collection<E>	ret;
		if(results!=null)
			ret	= new ArrayList<E>(results);
		else
			ret	= Collections.emptyList();
		return ret;
	}
	
	//-------- methods --------
	
	/**
	 *  Add an intermediate result.
	 */
	public void	addIntermediateResult(E result)
	{
//		if(result!=null && Object.class.equals(result.getClass()))
//			System.out.println("ires: "+this+" "+result);
		
	   	synchronized(this)
		{
        	if(resultavailable)
        	{
        		if(this.exception!=null)
        		{
//        			this.exception.printStackTrace();
            		throw new DuplicateResultException(DuplicateResultException.TYPE_EXCEPTION_RESULT, this, this.exception, result);
        		}
        		else
        		{
            		throw new DuplicateResultException(DuplicateResultException.TYPE_RESULT_RESULT, this, this.result, result);        			
        		}
        	}
	   	
        	addResult(result);
        	
//			if(listener instanceof IIntermediateResultListener)
//			{
//				scheduleNotification(listener, true, result);
//			}
			if(listeners!=null)
			{
				// Find intermediate listeners to be notified.
				for(int i=0; i<listeners.size(); i++)
				{
					if(listeners.get(i) instanceof IIntermediateResultListener)
					{
						scheduleNotification(listeners.get(i), true, result);
					}
				}
			}
		}

	   	resumeIntermediate();
		startScheduledNotifications();
	}
	
	/**
	 *  Add a result.
	 *  @param result The result.
	 */
	protected void addResult(E result)
	{
      	intermediate = true;
		if(results==null)
			results	= new ArrayList<E>();
		results.add(result);
	}
	
	/**
     *  Set the result. 
     *  Listener notifications occur on calling thread of this method.
     *  @param result The result.
     *  @return True if result was set.
     */
    public boolean	addIntermediateResultIfUndone(E result)
    {
    	synchronized(this)
		{
        	if(isDone())
        	{
        		return false;
        	}
        	else
        	{
        		addResult(result);
    			
    			if(listeners!=null)
    			{
    				// Find intermediate listeners to be notified.
    				for(int i=0; i<listeners.size(); i++)
    				{
    					if(listeners.get(i) instanceof IIntermediateResultListener)
    					{
    						scheduleNotification(listeners.get(i), true, result);
    					}
    				}
    			}
    		}
 		}

	   	resumeIntermediate();
    	startScheduledNotifications();
    	return true;
    }
	
	/**
     *  Set the result. 
     *  Listener notifications occur on calling thread of this method.
     *  @param result The result.
     */
    public void	setResult(Collection<E> result)
    {
//		System.out.println("setResult: "+this+" "+result);
    	
    	boolean ex = false;
    	synchronized(this)
		{
    		ex = intermediate;
//    		if(results!=null)
//    			ex = true;
		}
    	if(ex)
    	{
    		throw new RuntimeException("setResult() only allowed without intermediate results:"+results);
    	}
    	else
    	{
    		if(result!=null && !(result instanceof Collection))
    		{
    			throw new IllegalArgumentException("Result must be collection: "+result);
    		}
    		else
    		{
    			if(result!=null)
    				this.results = (Collection)result;
    			super.setResult(results);
    		}
    	}
    }
    
	/**
     *  Set the result. 
     *  Listener notifications occur on calling thread of this method.
     *  @param result The result.
     */
    public boolean	setResultIfUndone(Collection<E> result)
    {
    	boolean ex = false;
    	synchronized(this)
		{
    		ex = intermediate;
//    		if(results!=null)
//    			ex = true;
		}
    	if(ex)
    	{
    		throw new RuntimeException("setResultIfUndone() only allowed without intermediate results:"+results);
    	}
    	else
    	{
    		if(result!=null && !(result instanceof Collection))
    		{
    			throw new IllegalArgumentException("Result must be collection: "+result);
    		}
    		else
    		{
    			this.results = (Collection)result;
    			return super.setResultIfUndone(result);
    		}
    	}
    }
    
    /**
     *  Declare that the future is finished.
     */
    public void setFinished()
    {
//		System.out.println("finished: "+this+" "+result);
    	
    	Collection	res;
    	synchronized(this)
    	{
    		res	= getIntermediateResults();
			// Hack!!! Set results to avoid inconsistencies between super.result and this.results,
    		// because getIntermediateResults() returns empty list when results==null.
    		if(results==null)
    		{
    			results	= res;
    		}
    	}
    	super.setResult(res);
    }
    
    /**
     *  Declare that the future is finished.
     */
    public boolean setFinishedIfUndone()
    {
    	Collection	res;
    	synchronized(this)
		{
        	if(isDone())
        	{
        		return false;
        	}
        	else
        	{
        		res	= getIntermediateResults();
    			// Hack!!! Set results to avoid inconsistencies between super.result and this.results,
        		// because getIntermediateResults() returns empty list when results==null.
        		if(results==null)
        		{
        			results	= res;
        		}
        	}
		}
    	super.setResultIfUndone(res);
    	return true;
    }
    
    /**
     *  Add a result listener.
     *  @param listsner The listener.
     */
    public void	addResultListener(IResultListener<Collection<E>> listener)
    {
//    	if(getClass().getName().indexOf("Delegating")!=-1)
//    		System.out.println("lis: "+listener.getClass()+" "+this);
    	
    	if(listener==null)
    		throw new RuntimeException();
    	
    	synchronized(this)
    	{
    		// If results==null its a subscription future and first results are already collected.
    		if(results!=null && intermediate && listener instanceof IIntermediateResultListener)
    		{
    			Object[]	inter = results.toArray();
	    		IIntermediateResultListener lis =(IIntermediateResultListener)listener;
	    		for(int i=0; i<inter.length; i++)
	    		{
	    			scheduleNotification(lis, true, inter[i]);
	    		}
    		}
    		
	    	if(resultavailable)
	    	{
	    		scheduleNotification(listener, false, null);
	    	}
	    	else
	    	{
//	    		if(this.listener==null)
//	    		{
//	    			this.listener	= listener;
//	    		}
//	    		else
	    		{
		    		if(listeners==null)
		    			listeners	= new ArrayList<IResultListener<Collection<E>>>();
		    		listeners.add(listener);
	    		}
	    	}
    	}

    	startScheduledNotifications();
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
    	
		ISuspendable	caller	= null;
    	synchronized(this)
    	{
    		Integer	index	= indices!=null ? indices.get(Thread.currentThread()) : null;
    		if(index==null)
    		{
    			index	= new Integer(0);
    		}
    		
    		ret	= results!=null && results.size()>index.intValue();
    		suspend	= !ret && !isDone();
    		if(suspend)
    		{
    	    	caller	= ISuspendable.SUSPENDABLE.get();
    	    	if(caller==null)
    	    	{
    		   		throw new RuntimeException("No suspendable element.");
    	    	}
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
    				caller.suspend(this, -1);
    	    	   	icallers.remove(caller);
    			}
    			// else already resumed.
    		}
	    	ret	= hasNextIntermediateResult();
    	}
    	
    	return ret;
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
    public E getNextIntermediateResult()
    {
    	return getNextIntermediateResult(null);
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
    public E getNextIntermediateResult(ISuspendable sus)
    {
    	Integer	index;
    	synchronized(this)
    	{
			index	= indices!=null ? indices.get(Thread.currentThread()) : null;
			index	= index==null ? new Integer(1) : new Integer(index.intValue()+1);
			
			if(indices==null)
			{
				indices	= new HashMap<Thread, Integer>();
			}
			indices.put(Thread.currentThread(), index);
    	}
		return doGetNextIntermediateResult(index.intValue()-1, sus);
    }
    
    /**
     *  Perform the get without increasing the index.
     */
    protected E doGetNextIntermediateResult(int index, ISuspendable sus)
    {
       	E	ret	= null;
    	boolean	suspend	= false;
    	
		ISuspendable	caller	= sus;
    	synchronized(this)
    	{
    		if(results!=null && results.size()>index)
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
    			if(caller==null)
    				caller	= ISuspendable.SUSPENDABLE.get();
    	    	if(caller==null)
    	    	{
    		   		throw new RuntimeException("No suspendable element.");
    	    	}
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
    				caller.suspend(this, -1);
    	    	   	icallers.remove(caller);
    			}
    			// else already resumed.
    		}
	    	ret	= doGetNextIntermediateResult(index, sus);
    	}
    	
    	return ret;
    }	
    
    /**
     *  Notify a result listener.
     *  @param listener The listener.
     */
    protected void notifyIntermediateResult(IIntermediateResultListener<E> listener, E result)
    {
    	listener.intermediateResultAvailable(result);
    }

    /**
     *  Notify a result listener.
     *  @param listener The listener.
     */
    protected void notifyListener(IResultListener<Collection<E>> listener)
    {
    	scheduleNotification(listener, false, null);
    	startScheduledNotifications();
    }
    
    /**
     *  Notify a result listener.
     *  @param listener The listener.
     */
    protected void doNotifyListener(IResultListener<Collection<E>> listener)
    {
//    	try
//    	{
			if(exception!=null)
			{
				listener.exceptionOccurred(exception);
			}
			else
			{
				if(listener instanceof IIntermediateResultListener)
				{
					IIntermediateResultListener lis = (IIntermediateResultListener)listener;
					Object[] inter = null;
					synchronized(this)
					{
						if(!intermediate && results!=null)
						{
							inter = results.toArray();
						}
					}
					if(inter!=null)
			    	{
			    		for(int i=0; i<inter.length; i++)
			    		{
			    			notifyIntermediateResult(lis, (E)inter[i]);
			    		}
			    	}
					lis.finished();
				}
				else
				{
					listener.resultAvailable(results); 
				}
			}
//    	}
//    	catch(Exception e)
//    	{
//    		e.printStackTrace();
//    	}
    }
    
    /**
     *  Schedule a listener notification.
     *  @param listener The listener to be notified.
     *  @param intermediate	True for intermediate result, false for final results.
     *  @param result	The intermediate result (if any).
     */
    protected void	scheduleNotification(IResultListener<Collection<E>> listener, boolean intermediate, Object result)
    {
    	synchronized(this)
    	{
    		if(scheduled==null)
    		{
    			scheduled	= new ArrayList();
    		}
    		scheduled.add(intermediate ? new Object[]{listener, result} : listener);
    	}
    }
    
    /**
     *  Start scheduled listener notifications if not already running.
     *  Must not be called from synchronized block.
     */
    protected void	startScheduledNotifications()
    {
    	boolean	notify	= false;
    	synchronized(this)
    	{
    		if(!notifying && scheduled!=null)
    		{
    			notifying	= true;
    			notify	= true;
    		}
    	}
    	
    	while(notify)
    	{
    		Object	next	= null;
        	synchronized(this)
        	{
        		if(scheduled.isEmpty())
        		{
        			notify	= false;
        			notifying	= false;
        			scheduled	= null;
        		}
        		else
        		{
        			next	=  scheduled.remove(0);
            	}
        	}
        	
        	try
        	{
	        	if(next!=null)
	        	{
	        		if(next instanceof IResultListener)
	        		{
	        			doNotifyListener((IResultListener<Collection<E>>)next);
	        		}
	        		else
	        		{
	        			notifyIntermediateResult((IIntermediateResultListener<E>)((Object[])next)[0], (E)((Object[])next)[1]);
	        		}
	        	}
        	}
        	catch(Exception e)
        	{
        		e.printStackTrace();
        	}
    	}
    }

    /**
     *  Resume also intermediate waiters.
     */
    protected void resume()
    {
    	super.resume();
    	resumeIntermediate();
    }
    
	/**
	 *  Resume after intermediate result.
	 */
	protected void resumeIntermediate()
	{
		synchronized(this)
		{
			ISuspendable[]	callers	= icallers!=null ? icallers.keySet().toArray(new ISuspendable[0]) : null;
		   	if(callers!=null)
		   	{
				for(ISuspendable caller: callers)
		    	{
		    		Object mon = caller.getMonitor()!=null? caller.getMonitor(): caller;
		    		synchronized(mon)
					{
		    			String	state	= icallers.get(caller);
		    			if(CALLER_SUSPENDED.equals(state))
		    			{
		    				// Only reactivate thread when previously suspended.
		    				caller.resume(this);
		    			}
		    			icallers.put(caller, CALLER_RESUMED);
					}
		    	}
			}
		}
	}
}
