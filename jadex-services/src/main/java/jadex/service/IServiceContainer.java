package jadex.service;

import jadex.commons.concurrent.IResultListener;

import java.util.Collection;

/**
 *  Interface for a service container. Allows
 *  fetching service per type (and name). 
 */
public interface IServiceContainer
{
	/**
	 *  Get the name of the container
	 *  @return The name of this container.
	 */
	public String getName();
	
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
	 *  Start the service.
	 */
	public void start();
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdown(IResultListener listener);
}
