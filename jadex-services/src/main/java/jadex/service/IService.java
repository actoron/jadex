package jadex.service;


/**
 *  The interface for platform services.
 */
public interface IService
{
	/**
	 *  Get the service identifier.
	 *  @return The service identifier.
	 */
	public IServiceIdentifier getServiceIdentifier();
	
	/**
	 *  Get a service property.
	 *  @return The service property (if any).
	 */
	public Object getProperty(String name);
}
