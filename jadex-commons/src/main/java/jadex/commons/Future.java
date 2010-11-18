package jadex.commons;

import jadex.commons.concurrent.IResultListener;

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
public class Future implements IFuture
{
	//-------- constants --------
	
	/** A caller is queued for suspension. */
	protected final String	CALLER_QUEUED	= "queued";
	
	/** A caller is resumed. */
	protected final String	CALLER_RESUMED	= "resumed";
	
	/** A caller is suspended. */
	protected final String	CALLER_SUSPENDED	= "suspended";
	
	//-------- attributes --------
	
	/** The result. */
	protected Object result;
	
	/** The exception (if any). */
	protected Exception exception;
//	protected Exception resultex;
	
	/** Flag indicating if result is available. */
	protected boolean resultavailable;
	
	/** The blocked callers. */
	protected Map callers;
	
	/** The listeners. */
	protected List listeners;
	
	//-------- constructors --------
	
	/**
	 *  Create a new future.
	 */
	public Future()
	{
	}
	
	/**
	 *  Create a future that is already done.
	 *  @param result	The result, if any.
	 */
	public Future(Object result)
	{
		this();
		setResult(result);
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
    public Object get(ISuspendable caller)
    {
    	return get(caller, -1);
    }

    // todo: this are always realtime timeouts, what about simulation clocks!
    /**
     *  Get the result - blocking call.
     *  @param timeout The timeout in millis.
     *  @return The future result.
     */
    public Object get(ISuspendable caller, long timeout)
    {
    	boolean suspend = false;
    	synchronized(this)
    	{
	    	if(!resultavailable)
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
//    				System.out.println(this+" caller suspending: "+caller+" "+mon);
    				caller.suspend(timeout);
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
    	
    	if(exception!=null)
    	{
    		throw exception instanceof RuntimeException ?(RuntimeException)exception 
    			:new RuntimeException(exception);
    	}
    	else
    	{
    	   	return result;
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
    		// Ignore exception when already continued?!
        	if(resultavailable)
        	{
        		if(this.exception!=null)
        			this.exception.printStackTrace();
//        		if(resultex!=null)
//        		{
//        			System.err.println("Result: "+result);
//        			resultex.printStackTrace();
//        		}
        		throw new RuntimeException(this.exception);
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
    public void	setExceptionIfUndone(Exception exception)
    {
    	synchronized(this)
		{
    		// If done just return.
        	if(resultavailable)
        		return;
        		
//        	System.out.println(this+" setResult: "+result);
        	this.exception = exception;
        	resultavailable = true;			
		}
    	
    	resume();
    }

    /**
     *  Set the result. 
     *  Listener notifications occur on calling thread of this method.
     *  @param result The result.
     */
    public void	setResult(Object result)
    {
    	synchronized(this)
		{
        	if(resultavailable)
        		throw new RuntimeException();
        	
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
     */
    public void	setResultIfUndone(Object result)
    {
    	synchronized(this)
		{
        	if(resultavailable)
        		return;
        	
//        	System.out.println(this+" setResult: "+result);
        	this.result = result;
        	resultavailable = true;			
		}
    	
    	resume();
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
		//    		System.out.println(this+" resume: "+caller+" "+mon);
		    		synchronized(mon)
					{
		    			Object	state	= callers.get(caller);
		    			if(CALLER_SUSPENDED.equals(state))
		    			{
		    				// Only reactivate thread when previously suspended.
		    				caller.resume();
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
	    		notifyListener((IResultListener)listeners.get(i));
	    	}
		}
	}
    
    /**
     *  Add a result listener.
     *  @param listsner The listener.
     */
    public void	addResultListener(IResultListener listener)
    {
    	if(listener==null)
    		throw new RuntimeException();
    	
    	boolean	notify	= false;
    	synchronized(this)
    	{
	    	if(resultavailable)
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
    protected void notifyListener(IResultListener listener)
    {
    	// todo: source?
    	try
    	{
			if(exception!=null)
			{
				listener.exceptionOccurred(this, exception);
			}
			else
			{
				listener.resultAvailable(this, result); 
			}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    /**
     *  Main for testing. 
     */
    public static void main(String[] args) throws Exception
    {
    	final Future f = new Future();
    
    	f.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				System.out.println(Thread.currentThread().getName()+": listener: "+result);
			}
			
			public void exceptionOccurred(Object source, Exception exception)
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
