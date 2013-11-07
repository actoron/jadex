package jadex.commons;

/**
 *  Helper class to remember stack traces.
 *  Prints out a warning, when used.
 */
public class DebugException extends RuntimeException
{
	//-------- static part --------
	
	/**
	 *  Print out warning, when class is loaded.
	 */
	static
	{
		System.err.println("Warning: Using debug exceptions.");
	}
	
	//-------- constructors --------
	
	/**
	 * 	Create a debug exception.
	 */
	public DebugException()
	{
		fillInStackTrace();
	}
	
	/**
	 * 	Create a debug exception.
	 */
	public DebugException(String msg)
	{
		super(msg);
		fillInStackTrace();
	}
	
//	public void printStackTrace()
//	{
//		Thread.dumpStack();
//		super.printStackTrace();
//	}
}
