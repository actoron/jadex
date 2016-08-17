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
//		System.out.println("to");
	}
	
	/**
	 *  Create a timeout exception.
	 */
	public TimeoutException(String message)
	{
		super(message);
	}
	
	/**
	 *  Create a timeout exception.
	 */
	public TimeoutException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	@Override
	public void printStackTrace()
	{
		super.printStackTrace();
	}
}
