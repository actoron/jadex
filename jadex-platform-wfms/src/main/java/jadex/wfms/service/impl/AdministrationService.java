package jadex.wfms.service.impl;

import jadex.commons.ThreadSuspendable;
import jadex.service.BasicService;
import jadex.service.IServiceContainer;
import jadex.service.SServiceProvider;
import jadex.wfms.client.IClient;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.listeners.IActivityListener;
import jadex.wfms.listeners.ILogListener;
import jadex.wfms.listeners.IProcessListener;
import jadex.wfms.listeners.LogEvent;
import jadex.wfms.listeners.ProcessEvent;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IAdministrationService;
import jadex.wfms.service.IAuthenticationListener;
import jadex.wfms.service.IWfmsClientService;

import java.security.AccessControlException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class AdministrationService extends BasicService implements IAdministrationService
{
	private IServiceContainer wfms;
	
	/** The log listeners */
	private Map logListeners;
	
	/** The process listeners */
	private Map processListeners;
	
	/** The user activities listeners */
	private Map activitiesListeners;
	
	public AdministrationService(final IServiceContainer wfms)
	{
		this.wfms = wfms;
		this.logListeners = new HashMap();
		this.processListeners = new HashMap();
		this.activitiesListeners = new HashMap();
		
		Logger.getLogger("Wfms").addHandler(new Handler()
		{
			
			public void publish(LogRecord record)
			{
				for (Iterator it = logListeners.entrySet().iterator(); it.hasNext(); )
				{
					Map.Entry entry = (Map.Entry) it.next();
					ILogListener listener = (ILogListener) entry.getValue();
					IAAAService as = (IAAAService) SServiceProvider.getService(wfms, IAAAService.class).get(new ThreadSuspendable());
					LogEvent evt = new LogEvent(String.valueOf(record.getMessage()));
					//if (as.accessEvent((IClient) entry.getKey(), evt))
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
		
		IAAAService as = (IAAAService) SServiceProvider.getService(wfms, IAAAService.class).get(new ThreadSuspendable());
		as.addAuthenticationListener(new IAuthenticationListener()
		{
			
			public void deauthenticated(IClient client)
			{
				IWfmsClientService wcs = (IWfmsClientService) SServiceProvider.getService(wfms, IWfmsClientService.class).get(new ThreadSuspendable());
				IActivityListener listener = (IActivityListener) activitiesListeners.get(client);
				if (listener != null)
					wcs.removeActivityListener(listener);
			}
			
			public void authenticated(IClient client)
			{
			}
		});
	}
	
	/**
	 * Returns the current activities for all users
	 * 
	 * @param client the client
	 * @return current activities for all users
	 */
	public Map getUserActivities(final IClient client)
	{
		if(!((IAAAService)SServiceProvider.getService(wfms, IAAAService.class).get(new ThreadSuspendable())).accessAction(client, IAAAService.ADMIN_REQUEST_ALL_ACTIVITIES))
			throw new AccessControlException("Not allowed: "+client);
		return ((IWfmsClientService) SServiceProvider.getService(wfms, IWfmsClientService.class).get(new ThreadSuspendable())).getUserActivities();
	}
	
	/**
	 * Terminates the activity of a user.
	 * 
	 * @param client the client issuing the termination request
	 * @param activity the activity
	 */
	public void terminateActivity(IClient client, IClientActivity activity)
	{
		if(!((IAAAService)SServiceProvider.getService(wfms, IAAAService.class).get(new ThreadSuspendable())).accessAction(client, IAAAService.ADMIN_TERMINATE_ACTIVITY))
			throw new AccessControlException("Not allowed: "+client);
		((IWfmsClientService) SServiceProvider.getService(wfms, IWfmsClientService.class).get(new ThreadSuspendable())).terminateActivity(activity);
	}
	
	/**
	 * Adds a user activities listener which will trigger for
	 * any activity event, even activities unrelated to the client.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public void addActivitiesListener(IClient client, IActivityListener listener)
	{
		if(!((IAAAService)SServiceProvider.getService(wfms, IAAAService.class).get(new ThreadSuspendable())).accessAction(client, IAAAService.ADMIN_ADD_ACTIVITIES_LISTENER))
			throw new AccessControlException("Not allowed: "+client);
		IWfmsClientService wcs = (IWfmsClientService) SServiceProvider.getService(wfms, IWfmsClientService.class).get(new ThreadSuspendable());
		wcs.addActivityListener(listener);
		activitiesListeners.put(client, listener);
	}
	
	/**
	 * Removes a user activities listener.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public void removeActivitiesListener(IClient client, IActivityListener listener)
	{
		if(!((IAAAService)SServiceProvider.getService(wfms, IAAAService.class).get(new ThreadSuspendable())).accessAction(client, IAAAService.ADMIN_REMOVE_ACTIVITIES_LISTENER))
			throw new AccessControlException("Not allowed: "+client);
		IWfmsClientService wcs = (IWfmsClientService) SServiceProvider.getService(wfms, IWfmsClientService.class).get(new ThreadSuspendable());
		wcs.removeActivityListener(listener);
		activitiesListeners.remove(client);
	}
	
	/**
	 * Adds a log listener to the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public void addLogListener(IClient client, ILogListener listener)
	{
		if(!((IAAAService)SServiceProvider.getService(wfms, IAAAService.class).get(new ThreadSuspendable())).accessAction(client, IAAAService.ADMIN_ADD_LOG_LISTENER))
			throw new AccessControlException("Not allowed: "+client);
		logListeners.put(client, listener);
	}
	
	/**
	 * Removes a log listener from the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public void removeLogListener(IClient client, ILogListener listener)
	{
		if(!((IAAAService)SServiceProvider.getService(wfms, IAAAService.class).get(new ThreadSuspendable())).accessAction(client, IAAAService.ADMIN_REMOVE_LOG_LISTENER))
			throw new AccessControlException("Not allowed: "+client);
		logListeners.remove(client);
	}
	
	/**
	 * Adds a log listener to the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public void addProcessListener(IClient client, IProcessListener listener)
	{
		if(!((IAAAService)SServiceProvider.getService(wfms, IAAAService.class).get(new ThreadSuspendable())).accessAction(client, IAAAService.ADMIN_ADD_PROCESS_LISTENER))
			throw new AccessControlException("Not allowed: "+client);
		processListeners.put(client, listener);
	}
	
	/**
	 * Removes a log listener from the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public void removeProcessListener(IClient client, IProcessListener listener)
	{
		if(!((IAAAService)SServiceProvider.getService(wfms, IAAAService.class).get(new ThreadSuspendable())).accessAction(client, IAAAService.ADMIN_REMOVE_PROCESS_LISTENER))
			throw new AccessControlException("Not allowed: "+client);
		processListeners.remove(client);
	}
	
	public synchronized void fireProcessFinished(Object id)
	{
		for (Iterator it = processListeners.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) it.next();
			IProcessListener listener = (IProcessListener) entry.getValue();
			IAAAService as = (IAAAService) SServiceProvider.getService(wfms, IAAAService.class).get(new ThreadSuspendable());
			ProcessEvent evt = new ProcessEvent(String.valueOf(id));
			//if (as.accessEvent((IClient) entry.getKey(), evt))
			listener.processFinished(evt);
		}
	}
}
