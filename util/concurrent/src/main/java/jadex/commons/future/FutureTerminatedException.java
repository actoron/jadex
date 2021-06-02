package jadex.commons.future;

/**
 *  Exception that signals that a future has been terminated.
 */
public class FutureTerminatedException extends RuntimeException
{
	/**
	 *  Create an exception.
	 */
    public FutureTerminatedException() 
    {
    }

    /**
	 *  Create an exception with message.
	 */
    public FutureTerminatedException(String message) 
    {
        super(message);
    }

    /**
   	 *  Create an exception with message and cause.
   	 */
    public FutureTerminatedException(String message, Throwable cause) 
    {
        super(message, cause);
    }

    /**
   	 *  Create an exception with cause.
   	 */
    public FutureTerminatedException(Throwable cause) 
    {
        super(cause);
    }
	
//	public void printStackTrace()
//	{
//		Thread.dumpStack();
//		super.printStackTrace();
//	}
}
