package jadex.service;

import java.util.Set;

import jadex.commons.IFuture;

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
	public IFuture getService(Class type);
	
	/**
	 *  Get all services of a typ.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IFuture getServices(Class type);
	
	/**
	 *  Get a service.
	 *  @param name The name.
	 *  @return The corresponding service.
	 */
//	public Object getService(Class type, String name);
//	public IFuture getService(Class type, String name);
	
	/**
	 *  Get the available service types.
	 *  @return The service types.
	 */
	public IFuture getServicesTypes();
	
	// todo: remove me?
	/**
	 *  Get all services for a type.
	 *  @param type The type.
	 */
	public IFuture getServicesOfType(final Class type, final Set visited);
	
	// todo: remove me?
	/**
	 *  Get service for a type.
	 *  @param type The type.
	 */
	public IFuture getServiceOfType(final Class type, final Set visited);
	
	/**
	 *  Get the name of the provider.
	 *  @return The name of this provider.
	 */
	public String getName();
}
