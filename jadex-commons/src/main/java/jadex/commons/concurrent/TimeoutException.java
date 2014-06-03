package jadex.commons.concurrent;

/**
 *  Jadex timout exception.
 *  Jadex does not use the JDK timeout exception, because it is checked.
 *
 */
public class TimeoutException	extends RuntimeException
{
	Exception e = null;
	
	/**
	 *  Create a timeout exception.
	 */
	public TimeoutException()
	{
		e = new RuntimeException();
		System.out.println("timeout");
//		Thread.dumpStack();
	}
	
	/**
	 *  Create a timeout exception.
	 */
	public TimeoutException(String message)
	{
		super(message);
		e = new RuntimeException();
//		Thread.dumpStack();
	}
	
//	/**
//	 *  Hack for finding print source.
//	 */
	public void printStackTrace()
	{
		e.printStackTrace();
//		Thread.dumpStack();
//		super.printStackTrace();
	}
}
