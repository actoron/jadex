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
	}
	
	/**
	 *  Create a timeout exception.
	 */
	public TimeoutException(String message)
	{
		super(message);
	}
}
