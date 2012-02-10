package jadex.commons.future;


import jadex.commons.DebugException;
import jadex.commons.concurrent.TimeoutException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Future that includes mechanisms for callback notification.
 *  This allows a caller to decide if 
 *  a) a blocking call to get() should be used
 *  b) a callback shall be invoked
 */
public class Future<E> implements IFuture<E>
{
	//-------- constants --------
	
	/** A caller is queued for suspension. */
	protected final String	CALLER_QUEUED	= "queued";
	
	/** A caller is resumed. */
	protected final String	CALLER_RESUMED	= "resumed";
	
	/** A caller is suspended. */
	protected final String	CALLER_SUSPENDED	= "suspended";
	
	/** Debug flag. */
	public static final boolean DEBUG = true;
	
	/** The empty future. */
	public static final IFuture	EMPTY	= new Future(null);
	
	/**
	 *  Get the empty future of some type.
	 *  @return The empty future.
	 */
	public static <T> IFuture<T> getEmptyFuture()
	{
		return (IFuture<T>)EMPTY;
	}
	
	//-------- attributes --------
	
	/** The result. */
	protected E result;
	
	/** The exception (if any). */
	protected Exception exception;
//	protected Exception resultex;
	
	/** Flag indicating if result is available. */
	protected boolean resultavailable;
	
	/** The blocked callers. */
	protected Map callers;
	
	/** The listeners. */
	protected List listeners;
	
	/** For capturing call stack of future creation. */
	// Only for debugging;
	protected Exception creation;
	
	/** For capturing call stack of first setResult/Exception call. */
	// Only for debugging;
	protected Exception first;
	
	//-------- constructors --------
	
	/**
	 *  Create a new future.
	 */
	public Future()
	{
    	if(DEBUG)
    	{
    		creation	= new DebugException("future creation");
    	}
	}
	
	/**
	 *  Create a future that is already done.
	 *  @param result	The result, if any.
	 */
	public Future(E result)
	{
		this();
		setResult(result);
	}
	
	/**
	 *  Create a future that is already failed.
	 *  @param exception	The exception.
	 */
	public Future(Exception exception)
	{
		this();
		setException(exception);
	}
	
	//-------- methods --------

	/**
     *  Test if done, i.e. result is available.
     *  @return True, if done.
     */
    public synchronized boolean isDone()
    {
    	return resultavailable;
    }

    /**
     *  Get the result - blocking call.
     *  @return The future result.
     */
    public E get(ISuspendable caller)
    {
    	return get(caller, -1);
    }

    /**
     *  Get the result - blocking call.
     *  @param timeout The timeout in millis.
     *  @return The future result.
     */
    public E get(ISuspendable caller, long timeout)
    {
    	boolean suspend = false;
    	synchronized(this)
    	{
	    	if(!isDone())
	    	{
	    	   	if(caller==null)
	    	   		throw new RuntimeException("No suspendable element.");
	//        		caller = new ThreadSuspendable(this);
	     
//	    	   	System.out.println(this+" suspend: "+caller);
	    	   	if(callers==null)
	    	   		callers	= Collections.synchronizedMap(new HashMap());
	    	   	callers.put(caller, CALLER_QUEUED);
	    	   	suspend = true;
	    	}
    	}
    	
    	if(suspend)
		{
	    	Object mon = caller.getMonitor()!=null? caller.getMonitor(): caller;
	    	synchronized(mon)
	    	{
    			Object	state	= callers.get(caller);
    			if(CALLER_QUEUED.equals(state))
    			{
    	    	   	callers.put(caller, CALLER_SUSPENDED);
//    	    	   	if(caller.toString().indexOf("ExtinguishFirePlan")!=-1)
//    	    	   		System.out.println("caller suspending: "+caller+", "+this);
    				caller.suspend(this, timeout);
//    	    	   	if(caller.toString().indexOf("ExtinguishFirePlan")!=-1)
//    	    	   		System.out.println("caller resumed: "+caller+", "+this);
    		    	if(exception!=null)
    		    	{
    		    		// Nest exception to have both calling and manually set exception stack trace.
//     		    		exception	= new RuntimeException("Exception when evaluating future", exception);
     		    		exception	= new RuntimeException(exception.getMessage(), exception);
     		    	}
//    				System.out.println(this+" caller awoke: "+caller+" "+mon);
    			}
    			// else already resumed.
    		}
    	}
    	
//    	if(result==null)
//    		System.out.println(this+" here: "+caller);
    	
    	synchronized(this)
    	{
	    	if(exception!=null)
	    	{
	    		throw exception instanceof RuntimeException? (RuntimeException)exception 
	    			:new RuntimeException(exception);
	    	}
	    	else if(isDone())
	    	{
	    	   	return result;
	    	}
	    	else
	    	{
	    		throw new TimeoutException("Timeout while waiting for future.");
	    	}
    	}
    }
    
    /**
     *  Set the exception. 
     *  Listener notifications occur on calling thread of this method.
     *  @param exception The exception.
     */
    public void	setException(Exception exception)
    {
    	synchronized(this)
		{
        	if(isDone())
        	{
        		if(this.exception!=null)
        		{
//        			this.exception.printStackTrace();
            		throw new DuplicateResultException(DuplicateResultException.TYPE_EXCEPTION_EXCEPTION, this, this.exception, exception);
        		}
        		else
        		{
            		throw new DuplicateResultException(DuplicateResultException.TYPE_RESULT_EXCEPTION, this, result, exception);        			
        		}
        	}
        	else if(DEBUG)
        	{
        		first	= new DebugException("first setException()");
        	}
        	
//        	System.out.println(this+" setResult: "+result);
        	this.exception = exception;
        	resultavailable = true;			
		}
    	
    	resume();
    }
    
    /**
     *  Set the exception. 
     *  Listener notifications occur on calling thread of this method.
     *  @param exception The exception.
     */
    public boolean setExceptionIfUndone(Exception exception)
    {
    	if(exception==null)
    		throw new IllegalArgumentException();
    	synchronized(this)
		{
    		// Return if is done. Should implement same logic as setResultIfUndone().
        	if(isDone())
        	{
        		return false;
        		// If done propagate exception.
//        		if(exception instanceof RuntimeException)
//        			throw (RuntimeException)exception;
//        		else
//        			throw new RuntimeException(exception);
        	}
        	else
        	{
//	        	System.out.println(this+" setResult: "+result);
        		this.exception = exception;
        		resultavailable = true;
            	if(DEBUG)
            	{
            		first	= new DebugException("first setExceptionIfUndone()");
            	}
        	}
		}
    	
    	resume();
    	return true;
    }

    /**
     *  Set the result. 
     *  Listener notifications occur on calling thread of this method.
     *  @param result The result.
     */
    public void	setResult(E result)
    {
    	// There is an exception when this is ok.
    	// In BDI when belief value is a future.
//    	if(result instanceof IFuture)
//    	{
//    		System.out.println("Internal error, future in future.");
//    		setException(new RuntimeException("Future in future not allowed."));
//    	}
    	
    	synchronized(this)
		{
        	if(isDone())
        	{
        		if(this.exception!=null)
        		{
//        			this.exception.printStackTrace();
            		throw new DuplicateResultException(DuplicateResultException.TYPE_EXCEPTION_RESULT, this, this.exception, result);
        		}
        		else
        		{
            		throw new DuplicateResultException(DuplicateResultException.TYPE_RESULT_RESULT, this, result, result);        			
        		}
        	}
        	else if(DEBUG)
        	{
        		first	= new DebugException("first setResult()");
        	}
        	
//        	System.out.println(this+" setResult: "+result);
        	this.result = result;
        	resultavailable = true;			
//        	this.resultex = new Exception();
		}
    	
    	resume();
    }
    
    /**
     *  Set the result. 
     *  Listener notifications occur on calling thread of this method.
     *  @param result The result.
     *  @return True if result was set.
     */
    public boolean	setResultIfUndone(E result)
    {
    	synchronized(this)
		{
        	if(isDone())
        	{
        		return false;
        	}
        	else
        	{
//	        	System.out.println(this+" setResult: "+result);
        		this.result = result;
        		resultavailable = true;	
            	if(DEBUG)
            	{
            		first	= new DebugException("first setResultIfUndone()");
            	}
        	}
		}
    	
    	resume();
    	return true;
    }

	/**
	 *  Resume after result or exception has been set.
	 */
	protected void resume()
	{
		synchronized(this)
		{
    	   	if(callers!=null)
    	   	{
				for(Iterator it=callers.keySet().iterator(); it.hasNext(); )
		    	{
		    		ISuspendable caller = (ISuspendable)it.next();
		    		Object mon = caller.getMonitor()!=null? caller.getMonitor(): caller;
		    		synchronized(mon)
					{
		    			Object	state	= callers.get(caller);
		    			if(CALLER_SUSPENDED.equals(state))
		    			{
		    				// Only reactivate thread when previously suspended.
//		    			   	if(caller.toString().indexOf("ExtinguishFirePlan")!=-1)
//		    			   		System.out.println("resume caller: "+caller+", "+this);
		    				caller.resume(this);
		    			}
		    			callers.put(caller, CALLER_RESUMED);
					}
		    	}
			}
		}
		
		if(listeners!=null)
		{
	    	for(int i=0; i<listeners.size(); i++)
	    	{
	    		notifyListener((IResultListener<E>)listeners.get(i));
	    	}
		}
	}
    
    /**
     *  Add a result listener.
     *  @param listsner The listener.
     */
    public void	addResultListener(IResultListener<E> listener)
    {
    	if(listener==null)
    		throw new RuntimeException();
    	
    	boolean	notify	= false;
    	synchronized(this)
    	{
	    	if(isDone())
	    	{
	    		notify	= true;
	    	}
	    	else
	    	{
	    		if(listeners==null)
	    			listeners	= new ArrayList();
	    		listeners.add(listener);
	    	}
    	}
    	if(notify)
    		notifyListener(listener);
    }
    
    /**
     *  Notify a result listener.
     *  @param listener The listener.
     */
    protected void notifyListener(IResultListener<E> listener)
    {
//    	try
//    	{
			if(exception!=null)
			{
				listener.exceptionOccurred(exception);
			}
			else
			{
				listener.resultAvailable(result); 
			}
//    	}
//    	catch(Exception e)
//    	{
//    		e.printStackTrace();
//    	}
    }
    
    /**
     *  Main for testing. 
     */
    public static void main(String[] args) throws Exception
    {
    	final Future f = new Future();
    
    	f.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				System.out.println(Thread.currentThread().getName()+": listener: "+result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
			}
		});
    	
    	Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					System.out.println(Thread.currentThread().getName()+": waiting for 1 sec");
					Thread.sleep(1000);
					System.out.println(Thread.currentThread().getName()+": setting result");
					f.setResult("my result");
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
    	t.start();
    	
    	System.out.println(Thread.currentThread().getName()+": waiting for result");
    	Object result = f.get(new ThreadSuspendable(new Object()));
    	System.out.println(Thread.currentThread().getName()+": result is: "+result);
    }
}
