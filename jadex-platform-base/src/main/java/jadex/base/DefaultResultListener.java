package jadex.base;

import jadex.commons.concurrent.IResultListener;

import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *  The default listener for just printing out result information.
 *  Is used as fallback when no other listener is available.
 */
public abstract class DefaultResultListener implements IResultListener
{
	//-------- attributes --------
	
	/** The logger. */
	protected Logger logger;
	
	/** The static instance. */
	private static IResultListener instance;
	
	//-------- constructors --------
	
	/**
	 *  Create a new listener.
	 *  @param logger The logger.
	 */
	public DefaultResultListener()
	{
		this.logger = LogManager.getLogManager().getLogger(""+this);
	}
	
	/**
	 *  Create a new listener.
	 *  @param logger The logger.
	 */
	public DefaultResultListener(Logger logger)
	{
		this.logger = logger;
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
			instance = new DefaultResultListener(Logger.getLogger("default"))
			{
				public void resultAvailable(Object source, Object result)
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
	 * /
	public void resultAvailable(Object source, Object result)
	{
		//logger.info(""+result);
	}*/
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void exceptionOccurred(Object source, Exception exception)
	{
		exception.printStackTrace();
		logger.severe(source+" exception occurred: "+exception);
	}
}
