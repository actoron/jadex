package jadex.commons;

import jadex.commons.concurrent.IResultListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *  Future that includes mechanisms for callback notification.
 *  This allows a caller to decide if 
 *  a) a blocking call to get() should be used
 *  b) a callback shall be invoked
 */
public class CallbackFuture implements Future
{
	//-------- attributes --------
	
	/** The result. */
	protected Object result;
	
	/** Flag indicating if result is available. */
	protected boolean resultavailable;
	
	/** The listeners. */
	protected List listeners;
	
	//-------- constructors --------
	
	/**
	 *  Create a new future.
	 */
	public CallbackFuture()
	{
//		this.threads = new ArrayList();
		this.listeners = new ArrayList();
	}
	
	//-------- methods --------

	
	/**
	 *  Try to cancel.
	 */
    public synchronized boolean cancel(boolean mayInterruptIfRunning)
    {
    	// todo: cancel?
    	return false;
    }

    /**
     *  Test if cancelled.
     */
    public synchronized boolean isCancelled()
    {
    	// todo: cancel?
    	return false;
    }

    /**
     *  Test if done.
     */
    public synchronized boolean isDone()
    {
    	return resultavailable;
    }

    /**
     *  Get the result - blocking call.
     */
    public synchronized Object get() throws InterruptedException, ExecutionException
    {
    	if(!resultavailable)
    	{
    		this.wait();
    	}
    	return result;
    }

    /**
     *  Get the result - blocking call.
     */
    public synchronized Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
    {
    	if(!resultavailable)
    	{
//    		threads.add(Thread.currentThread());
    		Thread.currentThread().wait(unit.toMillis(timeout));
    	}
    	return result;
    }
    
    /**
     *  Set the result. 
     *  Listener notifications occur on calling thread of this method.
     */
    public synchronized void setResult(Object result)
    {
    	if(resultavailable)
    		throw new RuntimeException();
    	
    	this.result = result;
    	resultavailable = true;
    	
    	this.notifyAll();
    	for(int i=0; i<listeners.size(); i++)
    	{
    		notifyListener((IResultListener)listeners.get(i));
    	}
    }
    
    /**
     *  Add a result listener.
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
     */
    protected void notifyListener(IResultListener listener)
    {
    	// todo: source?
    	// hack!
		if(result instanceof Exception)
			listener.exceptionOccurred(this, (Exception)result);
		else
			listener.resultAvailable(this, result); 
    }
    
    /**
     *  Main for testing. 
     */
    public static void main(String[] args) throws Exception
    {
    	final CallbackFuture f = new CallbackFuture();
    
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
    	Object result = f.get();
    	System.out.println(Thread.currentThread().getName()+": result is: "+result);
    }
}
