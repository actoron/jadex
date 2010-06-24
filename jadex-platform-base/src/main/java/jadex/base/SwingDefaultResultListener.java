package jadex.base;

import jadex.commons.concurrent.IResultListener;

import java.util.logging.Logger;

import javax.swing.SwingUtilities;

/**
 *  Result listener that redirects callbacks on the swing thread.
 */
public abstract class SwingDefaultResultListener extends DefaultResultListener
{
	//-------- attributes --------
	
	/** The static instance. */
	private static IResultListener instance;
	
	//-------- constructors --------
	
	/**
	 *  Create a new listener.
	 *  @param logger The logger.
	 */
	public SwingDefaultResultListener()
	{
	}
	
	/**
	 *  Create a new listener.
	 *  @param logger The logger.
	 */
	public SwingDefaultResultListener(Logger logger)
	{
		super(logger);
	}
	
	/**
	 *  Get the listener instance.
	 *  @return The listener.
	 */
	public static IResultListener getInstance()
	{
		// Hack! Implement that logger can be passed
		if(instance==null)
		{
			instance = new SwingDefaultResultListener()
			{
				public void customResultAvailable(Object source, Object result)
				{
				}
			};
		}
		return instance;
	}
	
	//-------- methods --------
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	final public void resultAvailable(final Object source, final Object result)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				customResultAvailable(source, result);
			}
		});
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	final public void exceptionOccurred(final Object source, final Exception exception)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				customExceptionOccurred(source, exception);
			}
		});
	}
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public abstract void customResultAvailable(Object source, Object result);
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void customExceptionOccurred(Object source, Exception exception)
	{
		super.exceptionOccurred(source, exception);
	}
}
