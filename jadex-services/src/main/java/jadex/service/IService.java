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
	public void startService();
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdownService(IResultListener listener);
}
