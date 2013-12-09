package jadex.commons.concurrent;

/**
 *  Jadex timout exception.
 *  Jadex does not use the JDK timeout exception, because it is checked.
 *
 */
public class TimeoutException	extends RuntimeException
{
	/**
	 *  Create a timeout exception.
	 */
	public TimeoutException()
	{
		System.out.println("timeout");
	}
	
	/**
	 *  Create a timeout exception.
	 */
	public TimeoutException(String message)
	{
		super(message);
	}
	
//	/**
//	 *  Hack for finding print source.
//	 */
//	public void printStackTrace()
//	{
//		Thread.dumpStack();
//		super.printStackTrace();
//	}
}
