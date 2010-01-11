package jadex.wfms.service;

import java.util.logging.Handler;
import java.util.logging.Logger;

import jadex.service.IService;
import jadex.wfms.client.IClient;
import jadex.wfms.client.IMonitoringListener;

public interface IMonitoringService extends IService
{
	/**
	 * Adds a monitoring listener to the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public void addListener(IClient client, IMonitoringListener listener);
	
	/**
	 * Removes a monitoring listener from the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public void removeListener(IClient client, IMonitoringListener listener);
}
