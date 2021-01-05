package jadex.commons.future;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import jadex.commons.ICommand;
import jadex.commons.SUtil;
import jadex.commons.functional.Consumer;
import jadex.commons.functional.Function;

/**
 *  Default implementation of an intermediate future.
 */
public class IntermediateFuture<E> extends Future<Collection <E>> implements IIntermediateFuture<E> 
{
	//-------- attributes --------
	
	/** The intermediate results. */
	protected List<E> results;
	
	/** Flag indicating that addIntermediateResult()has been called. */
	protected boolean intermediate;
	
	/** The blocked intermediate callers (caller->state). */
	protected Map<ISuspendable, String> icallers;
    
	/** The index of the next result for a thread. */
    protected Map<Thread, Integer>	indices;
    
    /** The max result count (if given by the producer). */
    protected int maxresultcnt = -1;
    
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
	   	doAddIntermediateResult(result, false);

	   	resumeIntermediate();
	}
	
	/**
     *  Set the result. 
     *  @param result The result.
     *  @return True if result was set.
     */
    public boolean addIntermediateResultIfUndone(E result)
    {
    	boolean	ret	= doAddIntermediateResult(result, true);

    	if(ret)
    		resumeIntermediate();
    	
    	return ret;
    }
	
    /**
     *  Set the result and schedule listener notifications.
     *  @return true, when the result was added (finished and undone otherwise).
     */
    protected boolean doAddIntermediateResult(final E result, boolean undone)
    {
    	boolean	ret	= true;
    	boolean	notify	= false;
    	synchronized(this)
    	{
	    	if(undone)
	    		this.undone = true;
	
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
	    			ret	= false;
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
	    	else
	    	{
	    		//if(this instanceof SubscriptionIntermediateDelegationFuture && result!=null && result instanceof String)//result.toString().indexOf("ServiceEvent")!=-1)
	    		//	System.out.println("store event: "+result+" "+listener);
	    		boolean stored = storeResult(result);
	    		
	    		if(!stored && listener==null && listeners==null)
	    			throw new RuntimeException("lost value");

	    		notify	= true;

	    		//if(listener!=null && getResultCount()==1)
	    		//	scheduleMaxNotification(null);
	    		
	    		scheduleNotification(new ICommand<IResultListener<Collection<E>>>()
				{
	    			@Override
	    			public void execute(IResultListener<Collection<E>> listener)
	    			{
		        		if(listener instanceof IIntermediateResultListener)
		        		{
		        			notifyIntermediateResult((IIntermediateResultListener<E>)listener, result);
		        		}
	    			}
				});
	    	}
    	}
    	
    	if(notify)
    		startScheduledNotifications();
    	
    	return ret;
    }

    
	/**
	 *  Add a result.
	 *  @param result The result.
	 */
	protected boolean storeResult(E result)
	{
//		if(result!=null && result.getClass().getName().indexOf("ChangeEvent")!=-1)
//			System.out.println("ires: "+this+" "+result);
      	intermediate = true;
		if(results==null)
			results	= new ArrayList<E>();
		results.add(result);
		if(maxresultcnt==getResultCount())
			setFinishedIfUndone();
		return true;
	}
	
	/**
     *  Set the result. 
     *  Listener notifications occur on calling thread of this method.
     *  @param result The result.
     */
	@Override
	protected synchronized boolean doSetResult(Collection<E> result, boolean undone)
	{
    	synchronized(this)
		{
        	if(intermediate)
        	{
        		throw new RuntimeException("setResult() only allowed without intermediate results: "+results);
        	}
       		boolean	ret	= super.doSetResult(result, undone);
       		if(ret)
       		{
       			this.results	= result!=null ? new ArrayList<>(result) : null;
       		}
       		return ret;
		}
    }

    /**
     *  Declare that the future is finished.
     */
    public void setFinished()
    {
    	//if(done.contains(this.toString()))
    	//	System.out.println("setFini: "+this);
    	done.add(this.toString());
    	//Thread.currentThread().dumpStack();
    	doSetFinished(false);
    	
    	resume();
    }
    
    //protected static Set<String> done = new HashSet<>();
    
    /**
     *  Declare that the future is finished.
     */
    public boolean setFinishedIfUndone()
    {
    	boolean	ret	= doSetFinished(true);
    	if(ret)
    		resume();
    	return ret;
    }

    /**
     *  Declare that the future is finished.
     */
    protected synchronized boolean	doSetFinished(boolean undone)
    {
    	boolean	 ret;
    	
    	Collection<E>	res	= getIntermediateResults();
    	ret	= super.doSetResult(res, undone);
		if(ret)
		{
			// Hack!!! Set results to avoid inconsistencies between super.result and this.results,
    		// because getIntermediateResults() returns empty list when results==null.
    		if(results==null)
    			results	= Collections.emptyList();
		}

    	return ret;
    }
    
    /**
     *  Add a result listener.
     *  @param listener The listener.
     */
    public void	addResultListener(IResultListener<Collection<E>> listener)
    {
    	if(listener==null)
    		throw new RuntimeException();
       	
    	boolean	scheduled = false;
    	
    	synchronized(this)
    	{
    		// If results==null its a subscription future and first results are already collected.
    		if(intermediate && listener instanceof IIntermediateResultListener)
    		{
	    		IIntermediateResultListener<E> lis = (IIntermediateResultListener<E>)listener;
	    		scheduled = scheduleMaxNotification(lis);
	    		//System.out.println("addRes scheduleAll: "+maxresultcnt+" "+this);

	    		if(results!=null && !results.isEmpty())
	    		{    			
	    			//System.out.println("notify scheduled: "+results);
	    			scheduled = true;
		    				    		
		    		for(final E result: results)
		    		{
		    			@SuppressWarnings("unchecked")
						ICommand<IResultListener<Collection<E>>> c = (ICommand<IResultListener<Collection<E>>>) ((Object) new ICommand<IIntermediateResultListener<E>>()
						{
		    				@Override
		    				public void execute(IIntermediateResultListener<E> listener)
		    				{
		    					// Use template method to allow overwriting (e.g. for tuple2future).
		    					notifyIntermediateResult(listener, result);
		    				}
						}); 
		    			scheduleNotification(lis, c);
		    		}
	    		}
    		}
    	}

    	if(scheduled)
    		startScheduledNotifications();

    	//if(!scheduled && maxresultcnt!=-1)
    	//	System.out.println("addRes scheduleAll NOT: "+maxresultcnt+" "+this);

    	
       	super.addResultListener(listener);
    }
    
    protected ICommand<IResultListener<Collection<E>>>	notcommand	= new ICommand<IResultListener<Collection<E>>>()
	{
		@Override
		public void execute(IResultListener<Collection<E>> listener)
		{
	    	// Special handling only required for finished() instead of resultAvailable()
			if(exception==null && listener instanceof IIntermediateResultListener)
			{
				// If non-intermediate future use -> send collection results as intermediate results
				if(!intermediate && results!=null)
				{
		    		for(E result: results)
		    		{
		    			notifyIntermediateResult((IIntermediateResultListener<E>)listener, result);
		    		}
				}
				
    			if(undone && listener instanceof IUndoneIntermediateResultListener)
				{
					((IUndoneIntermediateResultListener<E>)listener).finishedIfUndone();
				}
				else
				{
					((IIntermediateResultListener<E>)listener).finished();
				}
			}
				
			// Use default handling for exception and non-intermediate listeners
			else
			{
				IntermediateFuture.super.getNotificationCommand().execute(listener);
			}
		}
	};
	
    /**
     *  Get the notification command.
     */
    protected ICommand<IResultListener<Collection<E>>>	getNotificationCommand()
    {
    	return notcommand;
    }
    
	/**
	 * Add an result listener, which called on intermediate results.
	 * 
	 * @param listener The intermediate listener.
	 */
	public void addIntermediateResultListener(IIntermediateResultListener<E> listener)
	{
		addResultListener(listener);
	}
    
	/**
	 * Add a functional result listener, which called on intermediate results.
	 * Exceptions will be logged.
	 * 
	 * @param intermediateListener The intermediate listener.
	 * /
	public void addIntermediateResultListener(IFunctionalIntermediateResultListener<E> intermediateListener)
	{
		addIntermediateResultListener(intermediateListener, null, null);
	}*/

	/**
	 * Add a functional result listener, which called on intermediate results.
	 * Exceptions will be logged.
	 * 
	 * @param intermediateListener The intermediate listener.
	 * @param finishedListener The finished listener, called when no more
	 *        intermediate results will arrive. If <code>null</code>, the finish
	 *        event will be ignored.
	 * /
	public void addIntermediateResultListener(IFunctionalIntermediateResultListener<E> intermediateListener, IFunctionalIntermediateFinishedListener<Void> finishedListener)
	{
		addIntermediateResultListener(intermediateListener, finishedListener, null);
	}*/
	
	/**
	 * Add a functional result listener, which called on intermediate results.
	 * 
	 * @param ilistener The intermediate listener.
	 * @param elistener The listener that is called on exceptions. Passing
	 *        <code>null</code> enables default exception logging.
	 * /
    public void addIntermediateResultListener(IFunctionalIntermediateResultListener<E> ilistener, IFunctionalExceptionListener elistener)
    {    	
		addIntermediateResultListener(ilistener, null, elistener);
    }* /

    public void addIntermediateResultListener(final IFunctionalIntermediateResultListener<E> ilistener, final IFunctionalIntermediateFinishedListener<Void> flistener,
    	IFunctionalExceptionListener elistener)
    {
    	addIntermediateResultListener(ilistener, flistener, elistener, null);
    }
    	
	/**
	 * Add a functional result listener, which called on intermediate results.
	 * 
	 * @param ilistener The intermediate listener.
	 * @param flistener The finished listener, called when no more
	 *        intermediate results will arrive. If <code>null</code>, the finish
	 *        event will be ignored.
	 * @param elistener The listener that is called on exceptions. Passing
	 *        <code>null</code> enables default exception logging.
	 * /
	public void addIntermediateResultListener(final IFunctionalIntermediateResultListener<E> ilistener, final IFunctionalIntermediateFinishedListener<Void> flistener,
		IFunctionalExceptionListener elistener, final IFunctionalIntermediateResultCountListener rlistener)
	{
		final IFunctionalExceptionListener ielistener = (elistener == null) ? SResultListener.printExceptions(): elistener;
		addResultListener(new IntermediateDefaultResultListener<E>()
		{
			public void intermediateResultAvailable(E result)
			{
				ilistener.intermediateResultAvailable(result);
			}

			public void finished()
			{
				if(flistener != null) 
					flistener.finished();
			}

			public void exceptionOccurred(Exception exception)
			{
				ielistener.exceptionOccurred(exception);
			}
			
			public void maxResultCountAvailable(int max) 
			{
				rlistener.maxResultCountAvailable(max);
			}
		});
	}*/

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
    	return hasNextIntermediateResult(UNSET, false);
    }
    
    /**
     *  Check if there are more results for iteration for the given caller.
     *  If there are currently no unprocessed results and future is not yet finished,
     *  the caller is blocked until either new results are available and true is returned
     *  or the future is finished, thus returning false.
     *  
	 *  @param timeout The timeout in millis.
	 *  @param realtime Flag, if wait should be realtime (in constrast to simulation time).
     *  @return	True, when there are more intermediate results for the caller.
     */
    public boolean hasNextIntermediateResult(long timeout, boolean realtime)
    {
    	boolean	ret;
    	boolean	suspend;
		ISuspendable caller = ISuspendable.SUSPENDABLE.get();
	   	if(caller==null)
	   	{
	   		caller = new ThreadSuspendable();
	   	}
	   	
    	synchronized(this)
    	{
    		Integer	index	= indices!=null ? indices.get(Thread.currentThread()) : null;
    		if(index==null)
    		{
    			index	= Integer.valueOf(0);
    		}
    		
    		ret	= (results!=null && results.size()>index.intValue()) || isDone() && getException()!=null;
    		suspend	= !ret && !isDone();
    		if(suspend)
    		{
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
    				caller.suspend(this, timeout, realtime);
    	    	   	icallers.remove(caller);
    			}
    			// else already resumed.
    		}
	    	ret	= hasNextIntermediateResult(timeout, realtime);
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
    	return getNextIntermediateResult(false);
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
    public E getNextIntermediateResult(boolean realtime)
    {
    	return getNextIntermediateResult(UNSET, realtime);
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
    public E getNextIntermediateResult(long timeout, boolean realtime)
    {
    	Integer	index;
    	synchronized(this)
    	{
			index	= indices!=null ? indices.get(Thread.currentThread()) : null;
			index	= index==null ? Integer.valueOf(1) : Integer.valueOf(index.intValue()+1);
			
			if(indices==null)
				indices	= new HashMap<Thread, Integer>();
			indices.put(Thread.currentThread(), index);
    	}
		return doGetNextIntermediateResult(index.intValue()-1, timeout, realtime);
    }
    
    /**
     *  Set the maximum number of results.
     *  @param max The maximum number of results.
     */
    public void setMaxResultCount(int max)
    {
    	//System.out.println("max set: "+max);
    	this.maxresultcnt = max;
    	
    	if(listener!=null)
    	{
    		scheduleMaxNotification(null);
    		startScheduledNotifications();
    	}
    	
    	if(getResultCount()==max)
    		setFinishedIfUndone();
    }
    
    /**
     * 
     * @param max
     */
    protected boolean scheduleMaxNotification(IResultListener<Collection<E>> lis)
    {
    	boolean donotify = false;
    	
    	if(maxresultcnt!=-1)
    	{
    		donotify = true;
    		//System.out.println("donotify max: "+maxresultcnt+" "+lis);
    		
    		ICommand<IResultListener<Collection<E>>> com = new ICommand<IResultListener<Collection<E>>>()
			{
				@Override
				public void execute(IResultListener<Collection<E>> listener)
				{
					//System.out.println("notific: "+listener);
	        		if(listener instanceof IIntermediateResultListener)
	        		{
	        			((IIntermediateResultListener)listener).maxResultCountAvailable(maxresultcnt);
	        		}
				}
				
				@Override
				public String toString() 
				{
					return "notifyMaxCount";
				}
			};
    		
			// Important! two methods fitting scheduleNotification(lis, com) :-(
    		if(lis==null)
    			scheduleNotification(com);
    		else
    			scheduleNotification(lis, com);
    	}
//    	else
//    	{
//    		System.out.println("max: "+maxresultcnt+" "+lis);
//    	}
    	
    	return donotify;
    }
    
    /** 
     *  Get the number of results already collected.
     *  @return The number of results.
     */
    protected int getResultCount()
    {
    	return results!=null? results.size(): 0;
    }
    
    /**
     *  Perform the get without increasing the index.
     */
    protected E doGetNextIntermediateResult(int index, long timeout, boolean realtime)
    {
       	E	ret	= null;
    	boolean	suspend	= false;
		ISuspendable	caller	= ISuspendable.SUSPENDABLE.get();
		if(caller==null)
			caller	= new ThreadSuspendable();

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
    			if(getException()!=null)
    				throw SUtil.throwUnchecked(getException());
    			else
    				throw new NoSuchElementException("No more intermediate results.");
    		}
    		else
    		{
    			suspend	= true;
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
    				caller.suspend(this, timeout, realtime);
    	    	   	icallers.remove(caller);
    			}
    			// else already resumed.
    		}
	    	ret	= doGetNextIntermediateResult(index, timeout, realtime);
    	}
    	
    	return ret;
    }	
    
    /**
     *  Notify a result listener.
     *  @param listener The listener.
     */
    protected void notifyIntermediateResult(IIntermediateResultListener<E> listener, E result)
    {
    	if(undone && listener instanceof IUndoneIntermediateResultListener)
    	{
    		((IUndoneIntermediateResultListener<E>)listener).intermediateResultAvailableIfUndone(result);
    	}
    	else
    	{
    		listener.intermediateResultAvailable(result);
    	}
    }

//    /**
//     *  Notify a result listener.
//     *  @param listener The listener.
//     */
//    protected void doNotifyListener(IResultListener<Collection<E>> listener)
//    {
////    	try
////    	{
//			if(exception!=null)
//			{
//				if(undone && listener instanceof IUndoneResultListener)
//				{
//					((IUndoneResultListener<E>)listener).exceptionOccurredIfUndone(exception);
//				}
//				else
//				{
//					listener.exceptionOccurred(exception);
//				}
//			}
//			else
//			{
//				if(listener instanceof IIntermediateResultListener)
//				{
//					IIntermediateResultListener lis = (IIntermediateResultListener)listener;
//					Object[] inter = null;
//					synchronized(this)
//					{
//						if(!intermediate && results!=null)
//						{
//							inter = results.toArray();
//						}
//					}
//					if(inter!=null)
//			    	{
//			    		for(int i=0; i<inter.length; i++)
//			    		{
//			    			notifyIntermediateResult(lis, (E)inter[i]);
//			    		}
//			    	}
//					if(undone && listener instanceof IUndoneIntermediateResultListener)
//					{
//						((IUndoneIntermediateResultListener<E>)listener).finishedIfUndone();
//					}
//					else
//					{
//						lis.finished();
//					}
//				}
//				else
//				{
//					if(undone && listener instanceof IUndoneResultListener)
//					{
//						((IUndoneResultListener)listener).resultAvailableIfUndone(results);
//					}
//					else
//					{
//						listener.resultAvailable(results); 
//					}
//				}
//			}
////    	}
////    	catch(Exception e)
////    	{
////    		e.printStackTrace();
////    	}
//    }
    

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
		    			String	state = icallers.get(caller);
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
	
	//-------- java 8 extensions --------
	
	public IFuture<Collection<E>> catchErr(final Consumer<? super Exception> consumer, Class<?> futuretype)
    {
		IResultListener reslis = new IntermediateEmptyResultListener()
		{
			public void exceptionOccurred(Exception exception)
			{
				 consumer.accept(exception);
			}
		};
		addResultListener(reslis);
		
		/*this.addResultListener(new IResultListener<E>()
		{
			@Override
			public void exceptionOccurred(Exception exception)
			{
				consumer.accept(exception);
			}
			
			@Override
			public void resultAvailable(E result)
			{
			}
		});*/
		
        return this;
    }
	
	// todo: subscriptions need special treatment for first listener
	
	public IIntermediateFuture<? extends E> next(Consumer<? super E> function)
	{
		addResultListener(new IntermediateEmptyResultListener<E>()
		{
			public void intermediateResultAvailable(E result)
			{
				function.accept(result);
			}
		});
		return this;
	}
	
	public IIntermediateFuture<? extends E> max(Consumer<Integer> function)
	{
		addResultListener(new IntermediateEmptyResultListener<E>()
		{
			public void maxResultCountAvailable(int max) 
			{
				function.accept(max);
			}
		});
		return this;
	}
	
	public IIntermediateFuture<? extends E> finished(Consumer<? super E> function)
	{
		addResultListener(new IntermediateEmptyResultListener<E>()
		{
			public void finished() 
			{
				function.accept(null);
			}
		});
		return this;
	}
	
	/**
	 *  Implements async loop and applies a an async function to each element.
	 *  @param function The function.
	 *  @return True result intermediate future.
	 */
	public <R> IIntermediateFuture<R> mapAsync(final Function<E, IFuture<R>> function)
    {
       return mapAsync(function, null);
    }
	
	/**
	 *  Implements async loop and applies a an async function to each element.
	 *  @param function The function.
	 *  @return True result intermediate future.
	 */
	public <R> IIntermediateFuture<R> mapAsync(final Function<E, IFuture<R>> function, Class<?> futuretype)
    {
        final IntermediateFuture<R> ret = futuretype==null? new IntermediateFuture<R>(): (IntermediateFuture)getFuture(futuretype);

        this.addIntermediateResultListener(new IIntermediateResultListener<E>()
        {
            public void resultAvailable(Collection<E> result)
            {
                for(E v: result)
                {
                    intermediateResultAvailable(v);
                }
                finished();
            }

            public void intermediateResultAvailable(E result)
            {
                IFuture<R> res = function.apply(result);
                res.addResultListener(new IResultListener<R>()
                {
                    public void resultAvailable(R result)
                    {
                        ret.addIntermediateResult(result);
                    }

                    public void exceptionOccurred(Exception exception)
                    {
                        ret.setExceptionIfUndone(exception);
                    }
                });
            }

            public void finished()
            {
                ret.setFinished();
            }

            public void exceptionOccurred(Exception exception)
            {
                ret.setException(exception);
            }
            
            public void maxResultCountAvailable(int count) 
            {
            	ret.setMaxResultCount(count);
            }
        });

        return ret;
    }
	
	/**
	 *  Implements async loop and applies a an async multi-function to each element.
	 *  @param function The function.
	 *  @return True result intermediate future.
	 */
	public <R> IIntermediateFuture<R> flatMapAsync(final Function<E, IIntermediateFuture<R>> function)
    {
        final IntermediateFuture<R> ret = new IntermediateFuture<R>();

        this.addIntermediateResultListener(new IIntermediateResultListener<E>()
        {
        	boolean fin = false;
        	int cnt = 0;
        	int num = 0;
        	
            public void resultAvailable(Collection<E> result)
            {
                for(E v: result)
                {
                    intermediateResultAvailable(v);
                }
                finished();
            }

            public void intermediateResultAvailable(E result)
            {
            	cnt++;
                IIntermediateFuture<R> res = function.apply(result);
                res.addResultListener(new IIntermediateResultListener<R>()
                {
                    public void intermediateResultAvailable(R result)
                    {
                    	ret.addIntermediateResult(result);
                    }
                    
                    public void finished()
                    {
                    	if(++num==cnt && fin)
                    	{
                    		ret.setFinished();
                    	}
                    }
                    
                    public void resultAvailable(Collection<R> result)
                    {
                    	for(R r: result)
                        {
                            intermediateResultAvailable(r);
                        }
                        finished();
                    }
                    
                    public void exceptionOccurred(Exception exception)
                    {
                    	ret.setExceptionIfUndone(exception);
                    }
                    
                    public void maxResultCountAvailable(int max) 
                    {
                    	ret.setMaxResultCount(max);
                    }
                });
            }

            public void finished()
            {
            	fin = true;
            	if(num==cnt)
            		ret.setFinished();
            }

            public void exceptionOccurred(Exception exception)
            {
                ret.setException(exception);
            }
            
            public void maxResultCountAvailable(int max) 
            {
            	ret.setMaxResultCount(max);
            }
        });

        return ret;
    }
}
