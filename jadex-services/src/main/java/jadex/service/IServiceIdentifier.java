package jadex.service;

/**
 *  Interface for service identifier.
 */
public interface IServiceIdentifier
{
	/**
	 *  Get the service provider identifier.
	 *  @return The provider id.
	 */
	public Object getProviderId();
	
	/**
	 *  Get the service name.
	 *  @return The service name.
	 */
	public Object getServiceName();
}
