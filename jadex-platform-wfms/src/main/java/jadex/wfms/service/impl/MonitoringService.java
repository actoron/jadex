package jadex.wfms.service.impl;

import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.wfms.client.IClient;
import jadex.wfms.client.IMonitoringListener;
import jadex.wfms.client.IWorkitemListener;
import jadex.wfms.client.LogEvent;
import jadex.wfms.client.ProcessFinishedEvent;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IMonitoringService;

import java.security.AccessControlException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class MonitoringService implements IMonitoringService
{
	private IServiceContainer wfms;
	private Map listeners;
	
	public MonitoringService(final IServiceContainer wfms)
	{
		this.wfms = wfms;
		Logger.getLogger("Wfms").addHandler(new Handler()
		{
			
			public void publish(LogRecord record)
			{
				for (Iterator it = listeners.entrySet().iterator(); it.hasNext(); )
				{
					Map.Entry entry = (Map.Entry) it.next();
					IMonitoringListener listener = (IMonitoringListener) entry.getValue();
					IAAAService as = (IAAAService) wfms.getService(IAAAService.class);
					LogEvent evt = new LogEvent(String.valueOf(record.getMessage()));
					if (as.accessEvent((IClient) entry.getKey(), evt))
						listener.logMessage(evt);
				}
			}
			
			public void flush()
			{
			}
			
			public void close() throws SecurityException
			{
			}
		});
		
		listeners = new HashMap();
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
	 * Adds a monitoring listener to the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public void addListener(IClient client, IMonitoringListener listener)
	{
		if(!((IAAAService)wfms.getService(IAAAService.class)).accessAction(client, IAAAService.ADD_LOG_HANDLER))
			throw new AccessControlException("Not allowed: "+client);
		listeners.put(client, listener);
	}
	
	/**
	 * Removes a monitoring listener from the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public void removeListener(IClient client, IMonitoringListener listener)
	{
		if(!((IAAAService)wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REMOVE_LOG_HANDLER))
			throw new AccessControlException("Not allowed: "+client);
		listeners.remove(client);
	}
	
	public synchronized void fireProcessFinished(Object id)
	{
		for (Iterator it = listeners.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) it.next();
			IMonitoringListener listener = (IMonitoringListener) entry.getValue();
			IAAAService as = (IAAAService) wfms.getService(IAAAService.class);
			ProcessFinishedEvent evt = new ProcessFinishedEvent(String.valueOf(id));
			if (as.accessEvent((IClient) entry.getKey(), evt))
				listener.processFinished(evt);
		}
	}
}
