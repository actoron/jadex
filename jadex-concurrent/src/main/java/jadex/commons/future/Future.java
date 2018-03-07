package jadex.commons.future;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jadex.commons.DebugException;
import jadex.commons.ErrorException;
import jadex.commons.ICommand;
import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.TimeoutException;
import jadex.commons.Tuple2;
import jadex.commons.functional.BiFunction;
import jadex.commons.functional.Consumer;
import jadex.commons.functional.Function;

/**
 *  Future that includes mechanisms for callback notification.
 *  This allows a caller to decide if 
 *  a) a blocking call to get() should be used
 *  b) a callback shall be invoked
 */
public class Future<E> implements IFuture<E>, IForwardCommandFuture
{
//	static int stackcount, maxstack;
//	static double	avgstack;
	
	//-------- constants --------
	
	/** Notification stack for unwinding call stack to topmost future. */
	public static final ThreadLocal<List<Tuple2<Future<?>, IResultListener<?>>>>	STACK	= new ThreadLocal<List<Tuple2<Future<?>,IResultListener<?>>>>();
	
	/** A caller is queued for suspension. */
	protected static final String	CALLER_QUEUED	= "queued";
	
	/** A caller is resumed. */
	protected static final String	CALLER_RESUMED	= "resumed";
	
	/** A caller is suspended. */
	protected static final String	CALLER_SUSPENDED	= "suspended";
	
	/** Debug flag. */
	// Hack!!! Non-final to be setable from Starter 
	public static boolean DEBUG = true;
	
	/** Disable Stack unfolding for easier debugging. */
	// Hack!!! Non-final to be setable from Starter 
	public static boolean NO_STACK_COMPACTION = true;
	
	/** Constant for no timeout. */
	public static final long NONE = -1;
	
	/** Constant for unset. */
	public static final long UNSET = -2;
	
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
	protected volatile boolean resultavailable;
	
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
	
	/** The undone flag. */
	protected boolean undone;
	
	/** The list of commands. */
	protected Map<ICommand<Object>, IFilter<Object>> fcommands;
	
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
		
//		if(result instanceof Boolean)
//		{
//			Thread.dumpStack();
//		}
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
    public boolean isDone()
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
	 *  @deprecated - From 3.0. Use the version without suspendable.
	 *  Will NOT use the suspendable given as parameter.
	 *  
	 *  Get the result - blocking call.
	 *  @return The future result.
	 */
	public E get(ThreadSuspendable sus)
	{
		return get(NONE); 
	}
	
	/**
	 *  Get the result - blocking call.
	 *  @return The future result.
	 */
	public E get()
	{
		// It is a critical point whether to use NONE or UNSET here
		// NONE is good for Jadex service calls which automatically terminate after a timeout, 
		// problem is with non-Jadex calls which could block infinitely
		// UNSET is not good for Jadex calls, because the service call and the get() call could use different timeouts.
		// For non-Jadex calls this behavior avoids ever blocking calls and is good.
		//return get(UNSET); 
		return get(NONE); 
	}
	
	/**
	 *  Get the result - blocking call.
	 *  @param realtime Flag, if wait should be realtime (in constrast to simulation time).
	 *  @return The future result.
	 */
	public E get(boolean realtime)
	{
		return get(NONE); 
	}


	/**
	 *  Get the result - blocking call.
	 *  @param timeout The timeout in millis.
	 *  @return The future result.
	 */
	public E get(long timeout)
	{
		// Default for realtime is false because normally waits should
		// use the kind of wait of the internal clock. Outbound calls 
		// might use explicitly realtime to avoid immediate simulation timeouts.
		return get(timeout, false);
	}
	
	/**
	 *  Get the result - blocking call.
	 *  @param timeout The timeout in millis.
	 *  @param realtime Flag if timeout is realtime (in contrast to simulation time).
	 *  @return The future result.
	 */
	public E get(long timeout, boolean realtime)
	{
    	boolean suspend = false;
		ISuspendable caller = ISuspendable.SUSPENDABLE.get();

		if(caller==null) 
		{
			caller = new ThreadSuspendable();
		}

    	synchronized(this)
    	{
	    	if(!isDone())
	    	{
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
			if (SReflect.isAndroid()
					&& !SReflect.isAndroidTesting()
					&& ISuspendable.SUSPENDABLE.get() == null
					&& SUtil.androidUtils().runningOnUiThread()) {
				new Exception("Should not suspend Android UI main thread. Try executing your calls from a different thread! (see stacktrace)").printStackTrace();
			}

	    	Object mon = caller.getMonitor()!=null? caller.getMonitor(): caller;
	    	synchronized(mon)
	    	{
    			Object	state	= callers.get(caller);
    			if(CALLER_QUEUED.equals(state))
    			{
    	    	   	callers.put(caller, CALLER_SUSPENDED);
    	    	   	try
    	    	   	{
    	    	   		caller.suspend(this, timeout, realtime);
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
	    		if(exception instanceof RuntimeException)
	    		{
	    			throw (RuntimeException)exception;
	    		}
	    		else if(exception instanceof ErrorException)
	    		{
	    			// Special case to allow errors being set as exception result and thrown as errors.
	    			throw ((ErrorException)exception).getError();
	    		}
	    		else
	    		{
	    			// Nest exception to have both calling and manually set exception stack trace.
	    			throw new RuntimeException(exception.getMessage(), exception);
	    		}
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
	 *  Set the exception (internal implementation for normal and if-undone).
	 */
    protected boolean	doSetException(Exception exception, boolean undone)
    {
    	if(exception==null)
    		throw new IllegalArgumentException();
    	synchronized(this)
		{
    		if(undone)
    		{
    			this.undone	= undone;
    		}
    		
        	if(isDone())
        	{
        		if(undone)
        		{
        			return false;
        		}
        		else if(this.exception!=null)
        		{
            		throw new DuplicateResultException(DuplicateResultException.TYPE_EXCEPTION_EXCEPTION, this, this.exception, exception);
        		}
        		else
        		{
            		throw new DuplicateResultException(DuplicateResultException.TYPE_RESULT_EXCEPTION, this, this.result, exception);        			
        		}
        	}
        	
      		this.exception = exception;
      		
    		resultavailable = true;		
    		if(DEBUG)
        	{
        		first	= new DebugException("first setException()");
        	}
        }
    	
    	resume();
    	
    	return true;
    }
	
    
    /**
     *  Set the exception. 
     *  Listener notifications occur on calling thread of this method.
     *  @param exception The exception.
     */
    public void	setException(Exception exception)
    {
    	doSetException(exception, false);
    }
    
    /**
     *  Set the exception. 
     *  Listener notifications occur on calling thread of this method.
     *  @param exception The exception.
     */
    public boolean setExceptionIfUndone(Exception exception)
    {
    	return doSetException(exception, true);
    }

    /**
     *  Set the result. 
     *  Listener notifications occur on calling thread of this method.
     *  @param result The result.
     */
    public void	setResult(E result)
    {
//    	if(result!=null && (SReflect.getField(result.getClass(), "isproxy")!=null
//    		|| Proxy.isProxyClass(result.getClass())))
//    	{
//    		System.out.println("proxy result: "+result);
////    		Thread.dumpStack();
//    	}
    	
    	doSetResult(result, false);
    	
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
    	boolean	ret	= doSetResult(result, true);
    	if(ret)
    	{
    		resume();
    	}
    	return ret;
    }

    /**
     *  Set the result without notifying listeners.
     */
    protected synchronized boolean doSetResult(E result, boolean undone)
    {
    	if(undone)
    		this.undone = true;

    	if(result instanceof Future)
    		Logger.getLogger("future").warning("Future as result of future not supported");
    	
    	// There is an exception when this is ok.
    	// In BDI when belief value is a future.
//    	if(result instanceof IFuture)
//    	{
//    		System.out.println("Internal error, future in future.");
//    		setException(new RuntimeException("Future in future not allowed."));
//    	}
    	
    	if(isDone())
    	{
    		if(undone)
    		{
    			return false;
    		}
    		else if(this.exception!=null)
    		{
        		throw new DuplicateResultException(DuplicateResultException.TYPE_EXCEPTION_RESULT, this, this.exception, result);
    		}
    		else
    		{
        		throw new DuplicateResultException(DuplicateResultException.TYPE_RESULT_RESULT, this, this.result, result);        			
    		}
    	}
    	else if(DEBUG)
    	{
    		first	= new DebugException("first result");
    	}
    	
    	this.result = result;
    	resultavailable = true;
    	
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
    		listener	= null; // avoid memory leaks
		}
		if(listeners!=null)
		{
	    	for(int i=0; i<listeners.size(); i++)
	    	{
	    		notifyListener(listeners.get(i));
	    	}
    		listeners	= null; // avoid memory leaks
		}
	}
	
	/**
	 *  Abort a blocking get call.
	 */
	public void abortGet(ISuspendable caller)
	{
//		System.out.println("abort get1");
		synchronized(this)
		{
//			System.out.println("abort get2");
			if(callers!=null && callers.containsKey(caller))
    	   	{
//				System.out.println("abort get3");
				Object mon = caller.getMonitor()!=null? caller.getMonitor(): caller;
	    		synchronized(mon)
				{
//	    			System.out.println("abort get4");
	    			String state = callers.get(caller);
	    			if(CALLER_SUSPENDED.equals(state))
	    			{
	    				// Only reactivate thread when previously suspended.
//	    				System.out.println("abort get5");
	    				caller.resume(this);
	    			}
	    			callers.put(caller, CALLER_RESUMED);
				}
			}
		}
	}

	
	/**
	 * Add an functional result listener, which is only called on success.
	 * Exceptions will be handled by DefaultResultListener.
	 * 
	 * @param sucListener The listener.
	 */
	public void addResultListener(IFunctionalResultListener<E> sucListener)
	{
		addResultListener(sucListener, null);
	}

	/**
	 * Add a result listener by combining an OnSuccessListener and an
	 * OnExceptionListener.
	 * 
	 * @param sucListener The listener that is called on success.
	 * @param exListener The listener that is called on exceptions. Passing
	 *        <code>null</code> enables default exception logging.
	 */
	public void addResultListener(IFunctionalResultListener<E> sucListener, IFunctionalExceptionListener exListener)
	{
		addResultListener(SResultListener.createResultListener(sucListener, exListener));
	}

    /**
     *  Add a result listener.
     *  @param listener The listener.
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
	    			if(undone && listener instanceof IUndoneResultListener)
					{
						((IUndoneResultListener)listener).exceptionOccurredIfUndone(exception);
					}
					else
					{
						listener.exceptionOccurred(exception);
					}
	    		}
	    		else
	    		{
	    			if(undone && listener instanceof IUndoneResultListener)
					{
						((IUndoneResultListener)listener).resultAvailableIfUndone(result);
					}
					else
					{
						listener.resultAvailable(result);
					}
	    		}
				while(!list.isEmpty())
				{
					Tuple2<Future<?>, IResultListener<?>>	tup	= list.remove(0);
					Future<?> fut	= tup.getFirstEntity();
					IResultListener<Object> lis = (IResultListener<Object>)tup.getSecondEntity();
					if(fut.exception!=null)
					{
						if(fut.undone && lis instanceof IUndoneResultListener)
						{
							((IUndoneResultListener)lis).exceptionOccurredIfUndone(fut.exception);
						}
						else
						{
							lis.exceptionOccurred(fut.exception);
						}
					}
					else
					{
//						int	len	= list.size();
						if(fut.undone && lis instanceof IUndoneResultListener)
						{
							((IUndoneResultListener)lis).resultAvailableIfUndone(fut.result);
						}
						else
						{
							lis.resultAvailable(fut.result);
						}
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
	 *  Send a (forward) command to the listeners.
	 *  @param command The command.
	 */
	public void sendForwardCommand(Object command)
	{
		if(fcommands!=null)
		{
			for(Map.Entry<ICommand<Object>, IFilter<Object>> entry: fcommands.entrySet())
			{
				IFilter<Object> fil = entry.getValue();
				if(fil==null || fil.filter(command))
				{
					ICommand<Object> com = entry.getKey();
					com.execute(command);
				}
			}
		}
		
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
	protected void notifyListenerCommand(IResultListener<E> listener, Object command)
	{
		if(listener instanceof IFutureCommandListener)
		{
			((IFutureCommandListener)listener).commandAvailable(command);
		}
		else
		{
//			System.out.println("Cannot forward command: "+listener+" "+command);
			Logger.getLogger("future").fine("Cannot forward command: "+listener+" "+command);
		}
	}
	
	/**
	 *  Add a forward command with a filter.
	 *  Whenever the future receives an info it will check all
	 *  registered filters.
	 */
	public void addForwardCommand(IFilter<Object> filter, ICommand<Object> command)
	{
		if(fcommands==null)
		{
			fcommands = new LinkedHashMap<ICommand<Object>, IFilter<Object>>();
		}
		fcommands.put(command, filter);
	}
	
	/**
	 *  Add a command with a filter.
	 *  Whenever the future receives an info it will check all
	 *  registered filters.
	 */
	public void removeForwardCommand(ICommand<Object> command)
	{
		if(fcommands!=null)
		{
			fcommands.remove(command);
		}
	}
	
	/**
	 *  Check, if the future has at least one listener.
	 */
	public boolean	hasResultListener()
	{
		return listener!=null;
	}
	
	//-------- java8 extensions --------
	
	public <T> IFuture<T> thenApply(final Function<? super E, ? extends T> function)
    {
        return thenApply(function, null);
    }
	
    public <T> IFuture<T> thenApply(final Function<? super E, ? extends T> function, Class<?> futuretype)
    {
		final Future<T> ret = getFuture(futuretype);

        this.addResultListener(new ExceptionDelegationResultListener<E, T>(ret)
        {
        	public void customResultAvailable(E result)
        	{
        		 T res = function.apply(result);
        		 ret.setResult(res);
        	}	
        });

        return ret;
    }
	
	public <T> IFuture<T> thenCompose(final Function<? super E, IFuture<T>> function)
    {
        return thenCompose(function, null);
    }
	
	public <T> IFuture<T> thenCompose(final Function<? super E, IFuture<T>> function, Class<?> futuretype)
    {
		final Future<T> ret = getFuture(futuretype);

        this.addResultListener(new ExceptionDelegationResultListener<E, T>(ret)
        {
        	public void customResultAvailable(E result)
        	{
        		 IFuture<T> res = function.apply(result);
                 res.addResultListener(SResultListener.delegate(ret));
        	}	
        });

        return ret;
    }
	
	public IFuture<Void> thenAccept(final Consumer<? super E> consumer)
    {
        return thenAccept(consumer, null);
    }
	
	public IFuture<Void> thenAccept(final Consumer<? super E> consumer, Class<?> futuretype)
    {
		final Future<Void> ret = getFuture(futuretype);

        this.addResultListener(new ExceptionDelegationResultListener<E, Void>(ret)
        {
        	public void customResultAvailable(E result)
        	{
        		 consumer.accept(result);
        		 ret.setResult(null);
        	}
        });

        return ret;
    }
	
	public <U,V> IFuture<V> thenCombine(final IFuture<U> other, final BiFunction<? super E,? super U, ? extends V> function, Class<?> futuretype)
    {
		final Future<V> ret = getFuture(futuretype);

        this.addResultListener(new ExceptionDelegationResultListener<E, V>(ret)
        {
        	public void customResultAvailable(final E e)
        	{
        		other.addResultListener(new ExceptionDelegationResultListener<U, V>(ret) {
        			public void customResultAvailable(U u)
        			{
        				ret.setResult(function.apply(e, u));
        			}
        		});
        	}
        });

        return ret;
    }
	
	public <U> IFuture<U> applyToEither(IFuture<E> other, final Function<E, U> fn, Class<?> futuretype)
	{
		final CounterResultListener<Void> exceptionCounter = new CounterResultListener<Void>(2, SResultListener.<Void>ignoreResults());
		final Future<Void> resultIndicator = new Future<Void>();
		final Future<U> ret = getFuture(futuretype);
		IResultListener<E> listener = new IResultListener<E>()
		{

			public void resultAvailable(E result)
			{
				synchronized(resultIndicator) {
					if (!resultIndicator.isDone()) {
						resultIndicator.setResult(null);
						U apply = fn.apply(result);
						ret.setResult(apply);
					}
				}
			}

			public void exceptionOccurred(Exception exception)
			{
				synchronized(exceptionCounter) {
					exceptionCounter.resultAvailable(null);
				
					if (exceptionCounter.getCnt() == 2) {
						ret.setException(exception);
					}
				}
			}
		};
		
		this.addResultListener(listener);
		other.addResultListener(listener);
		
		return ret;
	}

	public IFuture<Void> acceptEither(IFuture<E> other, final Consumer<E> action, Class<?> futuretype)
	{
		final CounterResultListener<Void> exceptionCounter = new CounterResultListener<Void>(2, SResultListener.<Void>ignoreResults());
		final Future<Void> resultIndicator = new Future<Void>();
		final Future<Void> ret = getFuture(futuretype);
		IResultListener<E> listener = new IResultListener<E>()
		{

			public void resultAvailable(E result)
			{
				synchronized(resultIndicator) {
					if (!resultIndicator.isDone()) {
						resultIndicator.setResult(null);
						action.accept(result);
						ret.setResult(null);
					}
				}
			}

			public void exceptionOccurred(Exception exception)
			{
				synchronized(exceptionCounter) {
					exceptionCounter.resultAvailable(null);
				
					if (exceptionCounter.getCnt() == 2) {
						ret.setException(exception);
					}
				}
			}
		};
		
		this.addResultListener(listener);
		other.addResultListener(listener);
		
		return ret;
	}
	
	
//	public <U,V> IFuture<V> thenCombineAsync(final IFuture<U> other, final BiFunction<? super E,? super U, IFuture<V>> function, Class<?> futuretype)
//    {
//		final Future<V> ret = getFuture(futuretype);
//
//        this.addResultListener(new ExceptionDelegationResultListener<E, V>(ret)
//        {
//        	public void customResultAvailable(final E e)
//        	{
//        		other.addResultListener(new ExceptionDelegationResultListener<U, V>(ret) {
//        			public void customResultAvailable(U u)
//        			{
//        				IFuture<V> res = function.apply(e, u);
//        				res.addResultListener(SResultListener.delegate(ret));
//        			}
//        		});
//        	}
//        });
//
//        return ret;
//    }
	
	/**
	 *  Sequential execution of async methods via implicit delegation.
	 *  @param function Function that takes the result of this future as input and delivers future(t). 
	 *  @param ret The 
	 *  @return Future of the result of the second async call (=ret).
	 */
//	public <T> IFuture<T> thenApplyAndDelegate(final Function<E, IFuture<T>> function, Class<?> futuretype, final Future<T> ret)
//    {
//        this.addResultListener(new ExceptionDelegationResultListener<E, T>(ret)
//        {
//        	public void customResultAvailable(E result)
//        	{
//        		 IFuture<T> res = function.apply(result);
//                 res.addResultListener(new DelegationResultListener<T>(ret));
//        	}	
//        });
//        return ret;
//    }
	
	/**
	 *  Sequential execution of async methods via implicit delegation.
	 *  @param futuretype The type of the result future (cannot be automatically determined).
	 *  @return Future of the result of the second async call.
	 */
	public <T> Future<T> getFuture(Class<?> futuretype)
    {
		Future<T> ret;
		if(futuretype==null)
		{
			ret = new Future<T>();
		}	
		else
		{
			try
			{
				ret = (Future<T>)futuretype.newInstance();
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		return ret;
    }
}
