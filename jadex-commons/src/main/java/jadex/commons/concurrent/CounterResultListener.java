package jadex.commons.concurrent;


/**
 *  Counter result listener for counting a specified number of resultAvailable calls.
 *  On each call the custom
 */
public abstract class CounterResultListener implements IResultListener
{
	//-------- attributes --------
	
	/** The number of sub listeners to wait for. */
	protected int num;
	
	/** The number of received callbacks. */
	protected int cnt;
	
	//-------- constructors --------
	
	/**
	 *  Create a new counter listener.
	 *  @param num The number of sub callbacks.
	 */
	public CounterResultListener(int num)
	{
		this.num = num;
		
		if(num==0)
			finalResultAvailable(null, null);
	}
	
	//-------- methods --------
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public final synchronized void resultAvailable(Object source, Object result)
	{
//		System.out.println("here: "+cnt+" "+num);
		if(++cnt==num)
		{
//			System.out.println("!!!");
			// todo: what about aggregated result?
//			listener.resultAvailable(source, result);
			finalResultAvailable(source, result);
		}
		else
		{
			intermediateResultAvailable(source, result);
		}
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public abstract void exceptionOccurred(Object source, Exception exception);
	
	/**
	 *  Called when the final result is available.
	 */
	public abstract void finalResultAvailable(Object source, Object result);
	
	/**
	 *  Method that can be overridden to do sth. on each
	 *  result available call. 
	 */
	public void intermediateResultAvailable(Object source, Object result)
	{
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
