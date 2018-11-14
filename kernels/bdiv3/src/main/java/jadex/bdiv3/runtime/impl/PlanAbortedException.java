package jadex.bdiv3.runtime.impl;


/**
 *  Plan has aborted.
 */
public class PlanAbortedException extends RuntimeException
{
	//-------- constructors --------

	/**
	 *  Create a new plan failure exception.
	 */
	public PlanAbortedException()
	{
		this(null, null);
	}

	/**
	 *  Create a new plan failure exception.
	 *  @param message The message.
	 */
	public PlanAbortedException(String message)
	{
		this(message, null);
	}

	/**
	 *  Create a new plan failure exception.
	 *  @param message The message.
	 *  @param cause The cause.
	 */
	public PlanAbortedException(String message, Throwable cause)
	{
		super(message==null? cause!=null?cause.getMessage():null : null, cause);
	}
	
	public void printStackTrace()
	{
		super.printStackTrace();
	}
}

