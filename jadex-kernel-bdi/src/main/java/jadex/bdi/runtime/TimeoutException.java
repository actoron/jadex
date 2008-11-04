package jadex.bdi.runtime;

/**
 *  A timeout exception is thrown by a plan when a wait
 *  operation was performed and the timeout occurred.
 *  Note: This exception will not be logged by the logger
 *  as it occurs in plans (normal plain failure).
 */
public class TimeoutException	extends BDIFailureException
{
	//-------- constructors --------

	/**
	 *  Create a new timeout exception.
	 */
	public TimeoutException()
	{
		this("Timeout occurred");
	}

	/**
	 *  Create a new timeout exception.
	 *  @param message The message.
	 */
	public TimeoutException(String message)
	{
		super(message, null);
	}
}