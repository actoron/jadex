package jadex.wfms.service;

import java.util.logging.Handler;
import java.util.logging.Logger;

import jadex.service.IService;
import jadex.wfms.client.IClient;

public interface IMonitoringService extends IService
{
	/**
	 * Returns the logger.
	 * @return the logger
	 */
	public Logger getLogger();
	
	/**
	 * Adds a log handler to the workflow management system.
	 * 
	 * @param client the client
	 * @param handler the log handler
	 */
	public void addLogHandler(IClient client, Handler handler);
	
	/**
	 * Removes a log handler from the workflow management system.
	 * 
	 * @param client the client
	 * @param handler the log handler
	 */
	public void removeLogHandler(IClient client, Handler handler);
}
