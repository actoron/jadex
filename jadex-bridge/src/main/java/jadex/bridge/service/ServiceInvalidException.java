package jadex.bridge.service;

/**
 *  Exception that is thrown when a service is called that is invalid.
 */
public class ServiceInvalidException extends RuntimeException
{
//	/**
//	 *  Create a new service invalid exception.
//	 */
//	public ServiceInvalidException()
//	{
//		super();
//	}
	
	/**
	 *  Create a new service invalid exception.
	 */
	public ServiceInvalidException(String message)
	{
		super(message);
	}
}
