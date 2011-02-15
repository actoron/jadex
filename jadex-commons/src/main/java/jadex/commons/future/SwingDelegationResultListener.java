package jadex.commons.future;

import javax.swing.SwingUtilities;

/**
 *  Delegation result listener that calls customResultAvailable and
 *  customExceptionOccurred on swing thread.
 */
public class SwingDelegationResultListener implements IResultListener
{
	//-------- attributes --------
	
	/** The future to which calls are delegated. */
	protected Future future;
	
	//-------- constructors --------
	
	/**
	 *  Create a new listener.
	 */
	public SwingDelegationResultListener(Future future)
	{
		this.future = future;
	}
	
	//-------- IResultListener --------
	
	/**
	 *  Called when the result is available.
	 * @param result The result.
	 */
	final public void resultAvailable(final Object result)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					customResultAvailable(result);
				}
				catch(Exception e)
				{
					// Could happen that overridden customResultAvailable method
					// first sets result and then throws exception (listener ex are catched).
					future.setExceptionIfUndone(e);
				}
			}
		});
	}
	
	/**
	 *  Called when an exception occurred.
	 * @param exception The exception.
	 */
	final public void exceptionOccurred(final Exception exception)
	{
//		exception.printStackTrace();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				customExceptionOccurred(exception);
			}
		});
	}
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void customResultAvailable(Object result)
	{
		future.setResult(result);
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void customExceptionOccurred(Exception exception)
	{
		future.setException(exception);
	}
}
