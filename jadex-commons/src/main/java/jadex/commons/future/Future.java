package jadex.commons.future;


import jadex.commons.DebugException;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.concurrent.TimeoutException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *  Future that includes mechanisms for callback notification.
 *  This allows a caller to decide if 
 *  a) a blocking call to get() should be used
 *  b) a callback shall be invoked
 */
public class Future<E> implements IFuture<E>, ICommandFuture
{
	static int stackcount, maxstack;
	static double	avgstack;
	
	//-------- constants --------
	
	/** Notification stack for unwinding call stack to topmost future. */
	public static ThreadLocal<List<Tuple2<Future<?>, IResultListener<?>>>>	STACK	= new ThreadLocal<List<Tuple2<Future<?>,IResultListener<?>>>>();
	
	/** A caller is queued for suspension. */
	protected final String	CALLER_QUEUED	= "queued";
	
	/** A caller is resumed. */
	protected final String	CALLER_RESUMED	= "resumed";
	
	/** A caller is suspended. */
	protected final String	CALLER_SUSPENDED	= "suspended";
	
	/** Debug flag. */
	public static boolean DEBUG = true;
	
	/** Disable Stack unfolding for easier debugging. */
	public static boolean NO_STACK_COMPACTION = false;
	
	/** The empty future. */
	public static final IFuture<?>	EMPTY	= new Future<Object>(null);
	
	/**
	 *  Get the empty future of some type.
	 *  @return The empty future.
	 */
	public static <T> IFuture<T> getEmptyFuture()
	{
		return new Future<T>((T)null);
	}
	
	//-------- attributes --------
	
	/** The result. */
	protected E result;
	
	/** The exception (if any). */
	protected Exception exception;
	
	/** Flag indicating if result is available. */
	protected boolean resultavailable;
	
	/** The blocked callers (caller->state). */
	protected Map<ISuspendable, String> callers;
	
	/** The first listener (for avoiding array creation). */
	protected IResultListener<E> listener;
	
	/** The listeners. */
	protected List<IResultListener<E>> listeners;
	
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
    		creation	= new DebugException("future creation: "+this);
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
	 *  Get the exception, if any.
	 *  @return	The exception, if any, or null if the future is not yet done or succeeded without exception.
	 */
	public synchronized Exception	getException()
	{
		return exception;
	}
	
	/**
	 *  Get the result - blocking call.
	 *  @return The future result.
	 */
	public E get()
	{
		return get(ISuspendable.SUSPENDABLE.get());
	}

	/**
	 *  Get the result - blocking call.
	 *  @param timeout The timeout in millis.
	 *  @return The future result.
	 */
	public E get(long timeout)
	{
		return get(ISuspendable.SUSPENDABLE.get(), timeout);
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
	    	   	{
	    	   		throw new RuntimeException("No suspendable element.");
	    	   	}
	     
	    	   	if(callers==null)
	    	   	{
	    	   		callers	= Collections.synchronizedMap(new HashMap<ISuspendable, String>());
	    	   	}
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
    	    	   	try
    	    	   	{
    	    	   		caller.suspend(this, timeout);
    	    	   	}
    	    	   	finally
    	    	   	{
    	    	   		callers.remove(caller);
    	    	   	}
    			}
    			// else already resumed.
    		}
    	}
    	
    	synchronized(this)
    	{
	    	if(exception!=null)
	    	{
	    		// Nest exception to have both calling and manually set exception stack trace.
	    		throw new RuntimeException(exception.getMessage(), exception);
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
            		throw new DuplicateResultException(DuplicateResultException.TYPE_RESULT_EXCEPTION, this, this.result, exception);        			
        		}
        	}
//        	else if(DEBUG)
//        	{
//        		first	= new DebugException("first setException()");
//        	}
        	
      		this.exception = exception;
      		
    		resultavailable = true;		
    		if(DEBUG)
        	{
        		first	= new DebugException("first setException()");
        	}
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
        		this.exception = exception;
//	        	System.out.println(this+" setResult: "+result);
            	
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
            		throw new DuplicateResultException(DuplicateResultException.TYPE_RESULT_RESULT, this, this.result, result);        			
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
				for(Iterator<ISuspendable> it=callers.keySet().iterator(); it.hasNext(); )
		    	{
		    		ISuspendable caller = it.next();
		    		Object mon = caller.getMonitor()!=null? caller.getMonitor(): caller;
		    		synchronized(mon)
					{
		    			String	state	= callers.get(caller);
		    			if(CALLER_SUSPENDED.equals(state))
		    			{
		    				// Only reactivate thread when previously suspended.
		    				caller.resume(this);
		    			}
		    			callers.put(caller, CALLER_RESUMED);
					}
		    	}
			}
		}
		
		if(listener!=null)
		{
    		notifyListener(listener);			
		}
		if(listeners!=null)
		{
	    	for(int i=0; i<listeners.size(); i++)
	    	{
	    		notifyListener(listeners.get(i));
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
	    		if(this.listener==null)
	    		{
	    			this.listener	= listener;
	    		}
	    		else
	    		{
	    			if(listeners==null)
	    				listeners	= new ArrayList<IResultListener<E>>();
	    			listeners.add(listener);
	    		}
	    	}
    	}
    	if(notify)
    	{
    		notifyListener(listener);
    	}
    }
    
    /**
     *  Notify a result listener.
     *  @param listener The listener.
     */
    protected void notifyListener(IResultListener<E> listener)
    {
//		int stack	= Thread.currentThread().getStackTrace().length;
//    	synchronized(Future.class)
//		{
//			stackcount++;
//			avgstack	= (avgstack*(stackcount-1)+stack)/stackcount;
//			if(stack>maxstack)
//			{
//				maxstack	= stack;
//				System.out.println("max: "+maxstack+", avg: "+avgstack);
////				Thread.dumpStack();
//			}
//		}
    	
    	if(NO_STACK_COMPACTION || STACK.get()==null || SUtil.isGuiThread())
    	{
    		List<Tuple2<Future<?>, IResultListener<?>>>	list	= new LinkedList<Tuple2<Future<?>, IResultListener<?>>>();
    		STACK.set(list);
    		try
    		{
	    		if(exception!=null)
	    		{
	    			listener.exceptionOccurred(exception);
	    		}
	    		else
	    		{
	    			listener.resultAvailable(result); 
	    		}
				while(!list.isEmpty())
				{
					Tuple2<Future<?>, IResultListener<?>>	tup	= list.remove(0);
					Future<?> fut	= tup.getFirstEntity();
					if(fut.exception!=null)
					{
						tup.getSecondEntity().exceptionOccurred(fut.exception);
					}
					else
					{
//						int	len	= list.size();
						((IResultListener)tup.getSecondEntity()).resultAvailable(fut.result);
//						System.out.println(this+": "+tup+ (list.size()>=len ? " -> "+list.subList(len, list.size()) : ""));
					}
				}
    		}
    		finally
    		{
    			// Make sure that stack gets removed also when exception occurs -> else no notifications would happen any more.
    			STACK.set(null);
    		}
    	}
    	else
    	{
    		STACK.get().add(new Tuple2<Future<?>, IResultListener<?>>(this, listener));
    	}
    }
    
    /**
	 *  Send a command to the listeners.
	 *  @param command The command.
	 */
	public void sendCommand(Type command)
	{
		if(listener!=null)
		{
    		notifyListenerCommand(listener, command);			
		}
		if(listeners!=null)
		{
	    	for(int i=0; i<listeners.size(); i++)
	    	{
	    		notifyListenerCommand(listeners.get(i), command);
	    	}
		}
	}
	
	/**
	 *  Notify the command listeners.
	 *  @param listener The listener.
	 *  @param command The command.
	 */
	protected void notifyListenerCommand(IResultListener<E> listener, Type command)
	{
		if(listener instanceof IFutureCommandListener)
		{
			((IFutureCommandListener)listener).commandAvailable(command);
		}
		else
		{
			System.out.println("Cannot forward command: "+listener+" "+command);
		}
	}
}
