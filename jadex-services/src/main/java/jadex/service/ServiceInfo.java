package jadex.service;

/**
 * 
 */
public class ServiceInfo
{
	/** The service. */
	protected IService service;
	
	/** The service type under which the service is registered. */
	protected Class type;

	/**
	 *  Create a new service info.
	 */
	public ServiceInfo(Class type, IService service)
	{
		this.type = type;
		this.service = service;
	}

	/**
	 *  Get the service.
	 *  @return the service.
	 */
	public IService getService()
	{
		return service;
	}

	/**
	 *  Get the type.
	 *  @return the type.
	 */
	public Class getType()
	{
		return type;
	}
	
}
