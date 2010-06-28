package jadex.service;

import jadex.commons.concurrent.IResultListener;

/**
 *  Interface for a service container. Allows
 *  fetching service per type (and name). 
 */
public interface IServiceContainer extends IServiceProvider
{
	/**
	 *  Get the name of the container
	 *  @return The name of this container.
	 */
	public String getName();
	
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
