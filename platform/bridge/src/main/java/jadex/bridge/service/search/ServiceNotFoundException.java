package jadex.bridge.service.search;

/**
 *  Exception to denote that a requested service was not found.
 */
public class ServiceNotFoundException extends RuntimeException
{
	/** The failed query. */
	protected ServiceQuery<?>	query;
	
	/**
	 *  Create a new service not found exception.
	 */
	public ServiceNotFoundException(ServiceQuery<?> query)
	{
		this(""+query);
		this.query	= query;
		//if(message!=null && message.indexOf("chat")!=-1)
		//	System.out.println("gotcha");
	}
	
	/**
	 *  Create a new service not found exception.
	 */
	public ServiceNotFoundException(String message)
	{
		super(message);
		//if(message!=null && message.indexOf("chat")!=-1)
		//	System.out.println("gotcha");
	}
	
	/**
	 *  Get the failed query, if any.
	 */
	public ServiceQuery<?>	getQuery()
	{
		return query;
	}
	
	public void printStackTrace()
	{
		Thread.dumpStack();
		super.printStackTrace();
	}
}
