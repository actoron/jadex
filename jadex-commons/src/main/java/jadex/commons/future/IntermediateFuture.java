package jadex.commons.future;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *  Default implementation of an intermediate future.
 */
public class IntermediateFuture<E> extends Future<Collection <E>> implements	IIntermediateFuture<E>
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
	   	
        	intermediate = true;

        	addResult(result);
        	
//			if(results==null)
//				results	= new ArrayList<E>();
//			results.add(result);
			
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

		startScheduledNotifications();
	}
	
	/**
	 *  Add a result.
	 *  @param result The result.
	 */
	protected void addResult(E result)
	{
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
        		intermediate = true;
        		
        		addResult(result);
//    			if(results==null)
//    				results	= new ArrayList<E>();
//    			results.add(result);
    			
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
    	super.setResult(res);
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
    		if(intermediate && listener instanceof IIntermediateResultListener)
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
    
//    /**
//     *  Get the result - blocking call.
//     *  @return The future result.
//     */
//    public E getIntermediate(ISuspendable caller)
//    {
//    	return getIntermediate(caller, -1);
//    }
//
//    /**
//     *  Get the result - blocking call.
//     *  @param timeout The timeout in millis.
//     *  @return The future result.
//     */
//    public E getIntermediate(ISuspendable caller, long timeout)
//    {
//    	boolean suspend = false;
//    	synchronized(this)
//    	{
//	    	if(!isDone())
//	    	{
//	    	   	if(caller==null)
//	    	   		throw new RuntimeException("No suspendable element.");
//	//        		caller = new ThreadSuspendable(this);
//	     
////	    	   	System.out.println(this+" suspend: "+caller);
//	    	   	if(callers==null)
//	    	   		callers	= Collections.synchronizedMap(new HashMap<ISuspendable, String>());
//	    	   	callers.put(caller, CALLER_QUEUED);
//	    	   	suspend = true;
//	    	}
//    	}
//    	
//    	if(suspend)
//		{
//	    	Object mon = caller.getMonitor()!=null? caller.getMonitor(): caller;
//	    	synchronized(mon)
//	    	{
//    			Object	state	= callers.get(caller);
//    			if(CALLER_QUEUED.equals(state))
//    			{
//    	    	   	callers.put(caller, CALLER_SUSPENDED);
////    	    	   	if(caller.toString().indexOf("ExtinguishFirePlan")!=-1)
////    	    	   		System.out.println("caller suspending: "+caller+", "+this);
//    				caller.suspend(this, timeout);
////    	    	   	if(caller.toString().indexOf("ExtinguishFirePlan")!=-1)
////    	    	   		System.out.println("caller resumed: "+caller+", "+this);
//    		    	if(exception!=null)
//    		    	{
//    		    		// Nest exception to have both calling and manually set exception stack trace.
////     		    		exception	= new RuntimeException("Exception when evaluating future", exception);
//     		    		exception	= new RuntimeException(exception.getMessage(), exception);
//     		    	}
////    				System.out.println(this+" caller awoke: "+caller+" "+mon);
//    			}
//    			// else already resumed.
//    		}
//    	}
//    	
////    	if(result==null)
////    		System.out.println(this+" here: "+caller);
//    	
//    	synchronized(this)
//    	{
//	    	if(exception!=null)
//	    	{
//	    		throw exception instanceof RuntimeException? (RuntimeException)exception 
//	    			:new RuntimeException(exception);
//	    	}
//	    	else if(isDone())
//	    	{
//	    	   	return result;
//	    	}
//	    	else
//	    	{
//	    		throw new TimeoutException("Timeout while waiting for future.");
//	    	}
//    	}
//    }
    
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
}
