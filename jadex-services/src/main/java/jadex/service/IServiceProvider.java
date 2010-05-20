package jadex.service;

import java.util.Collection;

/**
 *  Interface for service providers.
 */
public interface IServiceProvider
{
	/**
	 *  Get the first declared service of a given type.
	 *  @param type The type.
	 *  @return The corresponding service.
	 */
	public Object getService(Class type);
	
	/**
	 *  Get a service.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public Collection getServices(Class type);
	
	/**
	 *  Get a service.
	 *  @param name The name.
	 *  @return The corresponding service.
	 */
	public Object getService(Class type, String name);
	
	/**
	 *  Get the available service types.
	 *  @return The service types.
	 */
	public Class[] getServicesTypes();
}
