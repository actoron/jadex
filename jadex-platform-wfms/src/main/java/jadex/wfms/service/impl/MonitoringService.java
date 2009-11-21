package jadex.wfms.service.impl;

import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IMonitoringService;

import java.security.AccessControlException;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class MonitoringService implements IMonitoringService
{
	private IServiceContainer wfms;
	private Logger logger;
	
	public MonitoringService(IServiceContainer wfms)
	{
		this.wfms = wfms;
		this.logger = Logger.getLogger("Wfms");
	}
	
	/**
	 *  Start the service.
	 */
	public void start()
	{
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdown(IResultListener listener)
	{
	}
	
	/**
	 * Returns the logger.
	 * @return the logger
	 */
	public Logger getLogger()
	{
		return logger;
	}
	
	/**
	 * Adds a log handler to the workflow management system.
	 * 
	 * @param client the client
	 * @param handler the log handler
	 */
	public void addLogHandler(IClient client, Handler handler)
	{
		if(!((IAAAService)wfms.getService(IAAAService.class)).accessAction(client, IAAAService.ADD_LOG_HANDLER))
			throw new AccessControlException("Not allowed: "+client);
		logger.addHandler(handler);
	}
	
	/**
	 * Removes a log handler from the workflow management system.
	 * 
	 * @param client the client
	 * @param handler the log handler
	 */
	public void removeLogHandler(IClient client, Handler handler)
	{
		if(!((IAAAService)wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REMOVE_LOG_HANDLER))
			throw new AccessControlException("Not allowed: "+client);
		logger.removeHandler(handler);
	}
}
