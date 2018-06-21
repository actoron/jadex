package jadex.bdiv3.runtime;

/**
 *  A BDI failure exception indicates that a plan has failed.
 *  Note: This exception will not be logged by the logger
 *  as it occurs in plans (normal plain failure).
 *  Subclasses of this exception are used to refine the failure reason.
 */
public abstract class BDIFailureException	extends RuntimeException
{
	//-------- constructors --------

	/**
	 *  Create a new BDI failure exception.
	 *  @param message The message.
	 *  @param cause The parent exception (if any).
	 */
	public BDIFailureException(String message, Throwable cause)
	{
		super(message, cause);
	}
}