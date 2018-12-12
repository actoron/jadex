package jadex.bridge;

import jadex.bridge.service.IServiceIdentifier;


/**
 *  Thrown when operations are invoked after a service has been shut down.
 */
public class ServiceTerminatedException	extends RuntimeException
{
	//-------- attributes --------
	
	/** The service identifier. */
	protected IServiceIdentifier sid;
	
	//-------- constructors --------
	
	/**
	 *	Create a service termination exception.  
	 */
	public ServiceTerminatedException(IServiceIdentifier sid)
	{
		super(sid.getServiceName());
		this.sid = sid;
	}

	//-------- methods --------
	
	/**
	 *  Get the service identifier.
	 *  @return The service identifier.
	 */
	public IServiceIdentifier getServiceIdentifier()
	{
		return sid;
	}
}
