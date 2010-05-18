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
	//-------- attributes --------
	
	/** The result. */
	protected Object result;
	
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
		this.listeners = new ArrayList();
		this.callers = Collections.synchronizedMap(new HashMap());
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
	    	   	callers.put(caller, Boolean.FALSE);
	    	   	suspend = true;
	    	}
    	}
    	
    	Object mon = caller.getMonitor()!=null? caller.getMonitor(): caller;
    	synchronized(mon)
    	{
    		if(suspend)
    		{
    			boolean resumed = ((Boolean)callers.get(caller)).booleanValue();
    			if(!resumed)
    			{
    				caller.suspend(timeout);
//    				System.out.println(this+" caller awoke: "+caller+" "+mon);
    			}
    		}
    	}
    	
//    	if(result==null)
//    		System.out.println(this+" here: "+caller);
    	
    	if(result instanceof RuntimeException)
    		throw (RuntimeException)result;
    	
    	return result;
    }
    
    /**
     *  Set the result. 
     *  Listener notifications occur on calling thread of this method.
     *  @param result The result.
     */
    public synchronized void setResult(Object result)
    {
    	if(resultavailable)
    		throw new RuntimeException();
    	
//    	System.out.println(this+" setResult: "+result);
    	this.result = result;
    	resultavailable = true;
    	
    	for(Iterator it=callers.keySet().iterator(); it.hasNext(); )
    	{
    		ISuspendable caller = (ISuspendable)it.next();
    		Object mon = caller.getMonitor()!=null? caller.getMonitor(): caller;
//    		System.out.println(this+" resume: "+caller+" "+mon);
    		synchronized(mon)
			{
    			callers.put(caller, Boolean.TRUE);
    			caller.resume();
			}
    	}
    	for(int i=0; i<listeners.size(); i++)
    	{
    		notifyListener((IResultListener)listeners.get(i));
    	}
    }
    
    /**
     *  Add a result listener.
     *  @param listsner The listener.
     */
    public synchronized void addResultListener(IResultListener listener)
    {
    	if(listener==null)
    		throw new RuntimeException(); 
    	if(resultavailable)
    	{
    		notifyListener(listener);
    	}
    	else
    	{
    		listeners.add(listener);
    	}
    }
    
    /**
     *  Notify a result listener.
     *  @param listener The listener.
     */
    protected void notifyListener(IResultListener listener)
    {
    	// todo: source?
    	// hack!
		if(result instanceof Exception)
		{
			listener.exceptionOccurred(this, (Exception)result);
		}
		else
		{
			listener.resultAvailable(this, result); 
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
