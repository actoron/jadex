package jadex.commons;

/**
 *  Helper class to remember stack traces.
 *  Prints out a warning, when used.
 */
@SuppressWarnings("serial")
public class DebugException extends RuntimeException
{
	//-------- static part --------
	
	/** Additional debug exception on current thread to use as cause. */
	public static final ThreadLocal<Exception>	ADDITIONAL	= new ThreadLocal<>();
	
	/**
	 *  Print out warning, when class is loaded.
	 */
	static
	{
		System.err.println("Warning: Using debug exceptions.");
	}
	
	//-------- attributes --------
	
	/** The stack trace. */
//	protected StackTraceElement[] stacktrace;
	
	//-------- constructors --------
	
	/**
	 * 	Create a debug exception.
	 */
	public DebugException()
	{
		super(ADDITIONAL.get());
		fillInStackTrace();
	}
	
	/**
	 * 	Create a debug exception.
	 */
	public DebugException(String msg)
	{
		super(msg, ADDITIONAL.get());
		fillInStackTrace();
	}
	
//	public void printStackTrace()
//	{
//		Thread.dumpStack();
//		super.printStackTrace();
//	}
	
	public synchronized Throwable fillInStackTrace()
	{
		Throwable throwable = super.fillInStackTrace();
		// when getStackTrace() is called, super saves the stacktrace elements, so we don't have to.
		getStackTrace();
		return throwable;
	}
}
