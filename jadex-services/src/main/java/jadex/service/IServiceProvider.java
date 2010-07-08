package jadex.service;

import jadex.commons.IFuture;

import java.util.Set;

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
	public IFuture getService(Class type, IVisitDecider decider);
	
	/**
	 *  Get all services of a typ.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IFuture getServices(Class type,  IVisitDecider decider);
	
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
	public IFuture getServicesTypes(IVisitDecider decider);
	
	/**
	 *  Add a service to the platform.
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param name The name.
	 *  @param service The service.
	 */
	public IFuture addService(Class type, Object service);

	/**
	 *  Removes a service from the platform (shutdowns also the service).
	 *  @param name The name.
	 *  @param service The service.
	 */
	public IFuture removeService(Class type, Object service);
	
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
		
	// todo: remove me?
	/**
	 *  Get the name of the provider.
	 *  @return The name of this provider.
	 */
	public String getName();

}
