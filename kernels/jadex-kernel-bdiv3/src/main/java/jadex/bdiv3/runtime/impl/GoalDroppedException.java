package jadex.bdiv3.runtime.impl;

/**
 *  An exception that indicates an aborted goal i.e. neither failed nor succeeded.
 *  Note: This exception will only be logged at level info by the logger.
 */
public class GoalDroppedException	extends GoalFailureException
{
	//-------- constructors --------

	/**
	 *  Create a new goal failure exception.
	 */
	public GoalDroppedException()
	{
		this(null, null);
	}

	/**
	 *  Create a new goal failure exception.
	 *  @param message The message.
	 */
	public GoalDroppedException(String message)
	{
		this(message, null);
	}

	/**
	 *  Create a new plan failure exception.
	 *  @param message The message.
	 *  @param cause The cause.
	 */
	public GoalDroppedException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public void printStackTrace()
	{
		Thread.dumpStack();
		super.printStackTrace();
	}
}
