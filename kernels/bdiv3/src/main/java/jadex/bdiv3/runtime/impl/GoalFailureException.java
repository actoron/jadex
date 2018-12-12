package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.runtime.BDIFailureException;

/**
 *  An exception that may be thrown by a plan to
 *  indicate a subgoal failure.
 *  Note: This exception will not be logged by the logger
 *  as it occurs in plans (normal plain failure).
 */
public class GoalFailureException	extends BDIFailureException
{
	//-------- constructors --------

	/**
	 *  Create a new goal failure exception.
	 */
	public GoalFailureException()
	{
		this(null, null);
	}

	/**
	 *  Create a new goal failure exception.
	 *  @param message The message.
	 */
	public GoalFailureException(String message)
	{
		this(message, null);
	}

	/**
	 *  Create a new plan failure exception.
	 *  @param message The message.
	 *  @param cause The cause.
	 */
	public GoalFailureException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public void printStackTrace()
	{
		super.printStackTrace();
	}
}
