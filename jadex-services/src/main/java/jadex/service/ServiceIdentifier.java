package jadex.service;

/**
 *  Service identifier for uniquely identifying a service.
 *  Is composed of the container id and the service name.
 */
public class ServiceIdentifier implements IServiceIdentifier
{
	//-------- attributes --------
	
	/** The provider identifier. */
	protected Object providerid;
	
	/** The service name. */
	protected String servicename;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service identifier.
	 */
	public ServiceIdentifier()
	{
	}
	
	/**
	 *  Create a new service identifier.
	 */
	public ServiceIdentifier(Object providerid, String servicename)
	{
		this.providerid = providerid;
		this.servicename = servicename;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the service provider identifier.
	 *  @return The provider id.
	 */
	public Object getProviderId()
	{
		return providerid;
	}
	
	/**
	 *  Set the providerid.
	 *  @param providerid The providerid to set.
	 */
	public void setProviderId(Object providerid)
	{
		this.providerid = providerid;
	}
	
	/**
	 *  Get the service name.
	 *  @return The service name.
	 */
	public Object getServiceName()
	{
		return servicename;
	}
	
	/**
	 *  Set the servicename.
	 *  @param servicename The servicename to set.
	 */
	public void setServiceName(String servicename)
	{
		this.servicename = servicename;
	}

	/**
	 *  Get the hashcode.
	 *  @return The hashcode.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((providerid == null) ? 0 : providerid.hashCode());
		result = prime * result + ((servicename == null) ? 0 : servicename.hashCode());
		return result;
	}

	/**
	 *  Test if an object is equal to this one.
	 *  @param obj The object.
	 *  @return True, if equal.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof IServiceIdentifier)
		{
			IServiceIdentifier sid = (IServiceIdentifier)obj;
			ret = sid.getProviderId().equals(getProviderId()) && sid.getServiceName().equals(getServiceName());
		}
		return ret;
	}
	
	
}
