package jadex.bridge.service.search;

/**
 *  Exception to denote that a requested service was not found.
 */
public class ServiceNotFoundException extends RuntimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1578469236285544067L;

	/**
	 *  Create a new service not found exception.
	 */
	public ServiceNotFoundException(String message)
	{
		super(message);
//		if(message!=null && message.indexOf("ISuperpeerRegistrySynchronizationService")!=-1)
//			System.out.println("gotcha");
	}
	
//	public void printStackTrace()
//	{
//		Thread.dumpStack();
//		super.printStackTrace();
//	}
}
