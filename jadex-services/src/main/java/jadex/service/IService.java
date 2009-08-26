package jadex.service;

import jadex.commons.concurrent.IResultListener;

/**
 *  The interface for platform services.
 */
public interface IService
{
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
