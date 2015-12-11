package jadex.commons.transformation.binaryserializer;

/**
 * Reporter receiving errors encountered during decoding and throws them.
 */
public class DefaultErrorReporter implements IErrorReporter
{
	/** Error reporter constant. */
	public static final IErrorReporter DEFAULT_ERROR_REPORTER = new DefaultErrorReporter();
	
	/**
	 *  Method called when a decoding error occurs.
	 *  
	 *  @param e The exception occurred during decoding.
	 */
	public void exceptionOccurred(Exception e)
	{
		if (e instanceof RuntimeException)
		{
			throw (RuntimeException) e;
		}
		else
		{
			throw new RuntimeException(e);
		}
	}
}
