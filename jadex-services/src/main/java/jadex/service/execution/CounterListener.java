package jadex.service.execution;

import jadex.commons.concurrent.IResultListener;

/**
 *  Counter listener
 */
class CounterListener implements IResultListener
{
	//-------- attributes --------
	
	/** The listener to call when all callbacks have been received. */
	protected IResultListener listener;
	
	/** The number of sub listeners to wait for. */
	protected int num;
	
	/** The number of received callbacks. */
	protected int cnt;
	
	/** Boolean indicating if an exception occurred. */
	protected Exception exception;
	
	/**
	 *  Create a new counter listener.
	 *  @param num The number of sub callbacks.
	 */
	public CounterListener(int num, IResultListener listener)
	{
		assert num>0;
		assert listener!=null;
		
		this.num = num;
		this.listener = listener;
	}
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public synchronized void resultAvailable(Object result)
	{
		if(++cnt==num)
		{
			// todo: what about aggregated result?
			listener.resultAvailable(result);
		}
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public synchronized void exceptionOccurred(Exception exception)
	{
		// On first exception transfer exception.
		listener.exceptionOccurred(exception);
	}
}
