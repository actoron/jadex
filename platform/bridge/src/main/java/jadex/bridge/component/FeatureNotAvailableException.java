package jadex.bridge.component;

/**
 *  Exception to denote that a requested feature is not available.
 */
public class FeatureNotAvailableException extends RuntimeException
{
	/**
	 *  Create a new service not found exception.
	 */
	public FeatureNotAvailableException(String message)
	{
		super(message);
//		if(message!=null && message.indexOf("Pull")!=-1)
//		{
//			System.out.println("hhhggg");
//		}
	}
	
//	public void printStackTrace()
//	{
//		Thread.dumpStack();
//		super.printStackTrace();
//	}
}