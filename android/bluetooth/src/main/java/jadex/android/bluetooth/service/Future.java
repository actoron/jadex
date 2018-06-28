package jadex.android.bluetooth.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to pass asynchronous results.
 * @author Julian Kalinowski
 *
 */
public class Future implements IFuture {
	
	protected Object result;
	
	protected Exception exception;
	
	/**
	 * True, if a result is available, els false
	 */
	public boolean resultAvailable;
	
	protected List<IResultListener> listeners;
	
	/**
	 * Constructor
	 */
	public Future() {
	}
	
    /* (non-Javadoc)
	 * @see jadex.android.bluetooth.service.IFuture#setResult(java.lang.Object)
	 */
    @Override
	public void	setResult(Object result)
    {
    	synchronized(this)
		{
        	if(resultAvailable)
        	{
        		if(this.exception!=null)
        		{
        			throw new RuntimeException("Duplicate Result");
        			//            		throw new DuplicateResultException(DuplicateResultException.TYPE_EXCEPTION_RESULT, this, this.exception, result);
        		}
        		else
        		{
        			throw new RuntimeException("Duplicate Result");
//            		throw new DuplicateResultException(DuplicateResultException.TYPE_RESULT_RESULT, this, result, result);        			
        		}
        	}
        	this.result = result;
        	resultAvailable = true;
        	notifyListeners();
        	this.notifyAll();
		}
    }
    
    /* (non-Javadoc)
	 * @see jadex.android.bluetooth.service.IFuture#setException(java.lang.Exception)
	 */
    @Override
	public void	setException(Exception exception)
    {
    	synchronized(this)
		{
        	if(resultAvailable)
        	{
        		if(this.exception!=null)
        		{
//        			this.exception.printStackTrace();
        			throw new RuntimeException("Duplicate Result");
//            		throw new DuplicateResultException(DuplicateResultException.TYPE_EXCEPTION_EXCEPTION, this, this.exception, exception);
        		}
        		else
        		{
        			throw new RuntimeException("Duplicate Result");
//            		throw new DuplicateResultException(DuplicateResultException.TYPE_RESULT_EXCEPTION, this, result, exception);        			
        		}
        	}
        	
//        	System.out.println(this+" setResult: "+result);
        	this.exception = exception;
        	resultAvailable = true;	
        	notifyListeners();
        	this.notifyAll();
		}
    }
	
    /* (non-Javadoc)
	 * @see jadex.android.bluetooth.service.IFuture#addResultListener(jadex.android.bluetooth.service.IResultListener)
	 */
    @Override
	public void	addResultListener(IResultListener listener)
    {
    	if(listener==null)
    		throw new RuntimeException();
    	
    	boolean notify	= false;
    	synchronized(this)
    	{
	    	if(resultAvailable)
	    	{
	    		notify	= true;
	    	}
	    	else
	    	{
	    		if(listeners==null)
	    			listeners	= new ArrayList<IResultListener>();
	    		listeners.add(listener);
	    	}
    	}
    	if(notify)
    		notifyListener(listener);
    }
    
    protected void notifyListeners() {
    	if(listeners!=null)
		{
	    	for(int i=0; i<listeners.size(); i++)
	    	{
	    		notifyListener((IResultListener)listeners.get(i));
	    	}
		}
    }
    
    /**
     *  Notify a result listener.
     *  @param listener The listener.
     */
    protected void notifyListener(IResultListener listener)
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
    
	/* (non-Javadoc)
	 * @see jadex.android.bluetooth.service.IFuture#isDone()
	 */
    @Override
	public synchronized boolean isDone()
    {
    	return resultAvailable;
    }
    
//    public synchronized void waitForResult() {
//    	try {
//			this.wait();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//    }
}
