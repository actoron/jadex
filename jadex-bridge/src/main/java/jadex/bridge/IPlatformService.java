package jadex.bridge;

import jadex.commons.concurrent.IResultListener;


/**
 *  The interface for platform services.
 */
public interface IPlatformService
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
