package jadex.commons.future;


/**
 *  Counter result listener for counting a specified number of resultAvailable calls.
 */
public class CounterResultListener<E> implements IResultListener<E>
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
	
	//-------- constructors --------
	
	/**
	 *  Create a new counter listener.
	 *  @param num The number of sub callbacks.
	 */
	public CounterResultListener(int num, IResultListener<Void> delegate)
	{
		this(num, false, delegate);
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
			delegate.resultAvailable(null);
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
			delegate.resultAvailable(null);
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
	public final void exceptionOccurred(Exception exception)
	{
		boolean	notify	= false;
		
		boolean retry = false;
		if(!notified)
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
				delegate.resultAvailable(null);
			}
			else
			{
				delegate.exceptionOccurred(exception);
			}
		}
	}
	
	/**
	 *  Method that can be overridden to do sth. on each
	 *  result available call. 
	 */
	public void intermediateResultAvailable(E result)
	{
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
	 *  Get the number.
	 *  @return The number.
	 */
	public int getNumber()
	{
		return num;
	}

	/**
	 *  Get the cnt.
	 *  @return The cnt.
	 */
	public int getCnt()
	{
		return cnt;
	}
}
