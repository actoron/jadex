package jadex.commons.future;


/**
 *  Counter result listener for counting a specified number of resultAvailable calls.
 */
public class CounterResultListener<E> implements IResultListener<E>, IUndoneResultListener<E>
{
	//-------- attributes --------
	
	/** The number of sub listeners to wait for. */
	protected int num;
	
	/** The number of received callbacks. */
	protected int cnt;
	
	/** The delegate result listener. */
	protected IResultListener<Void> delegate;
	
	/** Flag to indicate that the delegate already has been notified. */
	protected boolean notified;
	
	/** The ignore failure flag. */
	protected boolean ignorefailures;
	
	/** The undone flag. */
	protected boolean undone;

	/** Listener that is called on intermediate results. */
	protected IFunctionalResultListener<E>	intermediateResultListener;
	
	//-------- constructors --------
	
	/**
	 * Create a new counter listener.
	 * 
	 * @param num The number of sub callbacks.
	 * @param countReachedListener Functional listener called when the count is
	 *        reached.
	 */
	public CounterResultListener(int num, IFunctionalResultListener<Void> countReachedListener)
	{
		this(num, countReachedListener, null);
	}

	/**
	 * Create a new counter listener.
	 * 
	 * @param num The number of sub callbacks.
	 * @param countReachedListener Functional listener called when the count is
	 *        reached.
	 * @param exListener The listener that is called on exceptions. Passing
	 *        <code>null</code> enables default exception logging.
	 */
	public CounterResultListener(int num, IFunctionalResultListener<Void> countReachedListener, IFunctionalExceptionListener exListener)
	{
		this(num, countReachedListener, null, exListener);
	}
	
	/**
	 * Create a new counter listener.
	 * 
	 * @param num The number of sub callbacks.
	 * @param countReachedListener Functional listener called when the count is
	 *        reached.
	 * @param intermediateResultListener Functional listener called on
	 *        intermediate results.
	 * @param exListener The listener that is called on exceptions. Passing
	 *        <code>null</code> enables default exception logging.
	 */
	public CounterResultListener(int num, IFunctionalResultListener<Void> countReachedListener, IFunctionalResultListener<E> intermediateResultListener, IFunctionalExceptionListener exListener)
	{
		this(num, false, countReachedListener, intermediateResultListener, exListener);
	}

	/**
	 * Create a new counter listener.
	 * 
	 * @param num The number of sub callbacks.
	 */
	public CounterResultListener(int num, IResultListener<Void> delegate)
	{
		this(num, false, delegate);
	}

	/**
	 * Create a new counter listener.
	 * 
	 * @param num The number of sub callbacks.
	 * @param ignorefailures Flag whether to ignore failures.
	 * @param countReachedListener Functional listener called when the count is
	 *        reached.
	 */
	public CounterResultListener(int num, boolean ignorefailures, IFunctionalResultListener<Void> countReachedListener)
	{
		this(num, ignorefailures, SResultListener.createResultListener(countReachedListener));
	}

	/**
	 * Create a new counter listener.
	 * 
	 * @param num The number of sub callbacks.
	 * @param ignorefailures Flag whether to ignore failures.
	 * @param countReachedListener Functional listener called when the count is
	 *        reached.
	 * @param exListener The listener that is called on exceptions. Passing
	 *        <code>null</code> enables default exception logging.
	 */
	public CounterResultListener(int num, boolean ignorefailures, IFunctionalResultListener<Void> countReachedListener, IFunctionalExceptionListener exListener)
	{
		this(num, ignorefailures, SResultListener.createResultListener(countReachedListener, exListener));
	}
	
	/**
	 * Create a new counter listener.
	 * 
	 * @param num The number of sub callbacks.
	 * @param ignorefailures Flag whether to ignore failures.
	 * @param countReachedListener Functional listener called when the count is
	 *        reached.
	 * @param intermediateResultListener Functional listener called on
	 *        intermediate results, can be <code>null</code>.
	 * @param exListener The listener that is called on exceptions. Passing
	 *        <code>null</code> enables default exception logging.
	 */
	public CounterResultListener(int num, boolean ignorefailures, IFunctionalResultListener<Void> countReachedListener, IFunctionalResultListener<E> intermediateResultListener, IFunctionalExceptionListener exListener)
	{
		this(num, ignorefailures, SResultListener.createResultListener(countReachedListener, exListener));
		this.intermediateResultListener = intermediateResultListener;
	}
	
	/**
	 *  Create a new counter listener.
	 *  @param num The number of sub callbacks.
	 */
	public CounterResultListener(int num, boolean ignorefailures, IResultListener<Void> delegate)
	{
		this.num = num;
		this.ignorefailures = ignorefailures;
		this.delegate = delegate;
		
		if(num==0)
		{
			this.notified = true;
			delegate.resultAvailable(null); // todo: undone??
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void resultAvailable(E result)
	{
//		System.out.println("here: "+hashCode()+" "+cnt+" "+num);
		boolean	notify	= false;
		synchronized(this)
		{
			if(!notified)
			{
//				System.out.println("resultAvailable: "+this+", "+this.sresults.size());
				notify	= ++cnt==num;
				notified = notify;
			}
		}
		
		if(notify)
		{
//			System.out.println("!!!");
			intermediateResultAvailable(result);
			if(undone && delegate instanceof IUndoneResultListener)
			{
				((IUndoneResultListener<E>)delegate).resultAvailableIfUndone(null);
			}
			else
			{
				delegate.resultAvailable(null);
			}
		}
		else if(!notified)
		{
			intermediateResultAvailable(result);
		}
	}
	
	/**
	 *  Called when an exception occurred.
	 * @param exception The exception.
	 */
	public void exceptionOccurred(Exception exception)
	{
		boolean	notify	= false;
		
		// This code is potentially wrong?!
		// notified could change its value between the synchronized blocks.
		
		boolean retry = false;
		boolean noti;
		synchronized(this)
		{
			noti = notified;
		}
		
		if(!noti)
			retry = intermediateExceptionOccurred(exception);
		
		synchronized(this)
		{
			if(!notified)
			{
				if(ignorefailures)
				{
					notify	= retry? cnt==num: ++cnt==num;
					notified = notify;
				}
				else 
				{
					notify	= true;
					notified	= true;
				}
			}
		}

		if(notify)
		{
//			System.out.println("exceptionOcurred: "+this+", "+this.sresults.size());
			
			if(ignorefailures)
			{
//				System.out.println("!!!");
				// todo: what about aggregated result?
//				listener.resultAvailable(source, result);
				if(undone && delegate instanceof IUndoneResultListener)
				{
					((IUndoneResultListener<E>)delegate).resultAvailableIfUndone(null);
				}
				else
				{
					delegate.resultAvailable(null);
				}
			}
			else
			{
				if(undone && delegate instanceof IUndoneResultListener)
				{
					((IUndoneResultListener<E>)delegate).exceptionOccurredIfUndone(exception);
				}
				else
				{
					delegate.exceptionOccurred(exception);
				}
			}
		}
	}
	
	/**
	 *  Method that can be overridden to do sth. on each
	 *  result available call. 
	 */
	public void intermediateResultAvailable(E result)
	{
		if(intermediateResultListener != null)
		{
			intermediateResultListener.resultAvailable(result);
		}
	}
	
	/**
	 *  Method that can be overridden to do sth. on each
	 *  exception that occurs. 
	 *  @return True, for retry the task (cnt is not increased);
	 */
	public boolean intermediateExceptionOccurred(Exception exception)
	{
		return false;
	}

	/**
	 *  Get the number of results this Listener is waiting for.
	 *  @return The expected number of results.
	 */
	public int getNumber()
	{
		return num;
	}
	
	/**
	 *  Set the number.
	 *  @param num The number.
	 */
	public void setNumber(int num)
	{
		this.num = num;
		if(num==0)
		{
			this.notified = true;
			delegate.resultAvailable(null); // todo: undone??
		}
	}

	/**
	 *  Get the current number of received results.
	 *  @return Number of results.
	 */
	public int getCnt()
	{
		return cnt;
	}
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void resultAvailableIfUndone(E result)
	{
		undone = true;
		resultAvailable(result);
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void exceptionOccurredIfUndone(Exception exception)
	{
		undone = true;
		exceptionOccurred(exception);
	}

	/**
	 *  Get the undone.
	 *  @return The undone.
	 */
	public boolean isUndone()
	{
		return undone;
	}
}
