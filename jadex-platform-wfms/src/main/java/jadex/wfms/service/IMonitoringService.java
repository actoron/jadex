package jadex.wfms.service;

import java.util.logging.Handler;

import jadex.service.IService;
import jadex.wfms.client.IClient;

public interface IMonitoringService extends IService
{
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
