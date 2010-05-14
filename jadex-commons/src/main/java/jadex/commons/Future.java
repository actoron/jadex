package jadex.commons;

import jadex.commons.concurrent.IResultListener;

import java.util.ArrayList;
import java.util.List;

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
	protected List callers;
	
	/** The listeners. */
	protected List listeners;
	
	//-------- constructors --------
	
	/**
	 *  Create a new future.
	 */
	public Future()
	{
		this.listeners = new ArrayList();
		this.callers = new ArrayList();
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
    public synchronized Object get(ISuspendable caller)
    {
    	return get(caller, -1);
    }

    /**
     *  Get the result - blocking call.
     *  @param timeout The timeout in millis.
     *  @return The future result.
     */
    public synchronized Object get(ISuspendable caller, long timeout)
    {
    	if(!resultavailable)
    	{
    	   	if(caller==null)
        		caller = new ThreadSuspendable(this);
     
    	   	callers.add(caller);
    		caller.suspend(timeout);
    	}
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
    	
    	this.result = result;
    	resultavailable = true;
    	
    	for(int i=0; i<callers.size(); i++)
    	{
    		((ISuspendable)callers.get(i)).resume();
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
    	Object result = f.get(null);
    	System.out.println(Thread.currentThread().getName()+": result is: "+result);
    }
}
