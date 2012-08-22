package jadex.platform.service.awareness.discovery;

/**
 *  Exception denoting that discovery cannot get access to
 *  the underlying 
 */
public class ConnectionException extends RuntimeException
{
	/**
	 *  Create a new exception.
	 */
    public ConnectionException(String message) 
    {
    	super(message);
    }

    /**
	 *  Create a new exception.
	 */
    public ConnectionException(Throwable cause) 
    {
        super(cause);
    }
    
    /**
	 *  Create a new exception.
	 */
    public ConnectionException(String message, Throwable cause) 
    {
        super(message, cause);
    }
}
