package jadex.bridge.service.component;

import jadex.bridge.service.BasicService;

/**
 *  Simple struct for storing a pojo/domain service 
 *  with its management part.
 */
public class ServiceInfo
{
	//-------- attributes --------
	
	/** The service domain object. */
	protected Object domainservice;
	
	/** The management object. */
	protected BasicService managementservice; 
	
	//-------- constructors --------
	
	/**
	 *  Create a new service info.
	 */
	public ServiceInfo(Object domainservice, BasicService managementservice)
	{
		this.domainservice = domainservice;
		this.managementservice = managementservice;
	}

	/**
	 *  Get the domain service.
	 *  @return The domain service.
	 */
	public Object getDomainService()
	{
		return domainservice;
	}

	/**
	 *  Get the management service.
	 *  @return The management service.
	 */
	public BasicService getManagementService()
	{
		return managementservice;
	}
}