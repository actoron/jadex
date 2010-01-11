package jadex.wfms.service.impl;

import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.wfms.client.ActivityEvent;
import jadex.wfms.client.IClient;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitemListener;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.client.ProcessFinishedEvent;
import jadex.wfms.client.Workitem;
import jadex.wfms.client.WorkitemEvent;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IClientService;
import jadex.wfms.service.IExecutionService;
import jadex.wfms.service.IModelRepositoryService;
import jadex.wfms.service.IMonitoringService;
import jadex.wfms.service.IProcessDefinitionService;
import jadex.wfms.service.IWfmsClientService;

import java.security.AccessControlException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 
 */
public class ClientConnector implements IClientService, IWfmsClientService
{
	
	private IServiceContainer wfms;
	
	private Map workitemQueues;
	
	private Map userActivities;
	
	private Map processWorkitemListeners;
	
	private Map workitemQueueListeners;
	
	public ClientConnector(IServiceContainer wfms)
	{
		this.wfms = wfms;
		processWorkitemListeners = new HashMap();
		workitemQueues = new HashMap();
		userActivities = new HashMap();
		workitemQueueListeners = new HashMap();
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
	
	public synchronized void queueWorkitem(IWorkitem workitem, IResultListener listener)
	{
		Set workitems = (Set) workitemQueues.get(workitem.getRole());
		if (workitems == null)
		{
			workitems = new HashSet();
			workitemQueues.put(workitem.getRole(), workitems);
		}
		workitems.add(workitem);
		if (listener != null)
			processWorkitemListeners.put(workitem, listener);
		fireWorkitemAddedEvent(workitem);
	}
	
	/**
	 * Requests the Process Definition Service
	 * 
	 * @param client the client
	 * @return the process definition service
	 */
	public synchronized IProcessDefinitionService getProcessDefinitionService(IClient client)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_PD_SERVICE))
			return null;
		
		return (IProcessDefinitionService) wfms.getService(IProcessDefinitionService.class);
	}
	
	/**
	 * Requests the Monitoring Service
	 * 
	 * @param client the client
	 * @return the process definition service
	 */
	public synchronized IMonitoringService getMonitoringService(IClient client)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_MONITORING_SERVICE))
			return null;
		return (IMonitoringService) wfms.getService(IMonitoringService.class);
	}
	
	/**
	 * Authenticated a new client.
	 * @param client the new client
	 * @return true, if the client has been successfully authenticated.
	 */
	public synchronized boolean authenticate(IClient client)
	{
		IAAAService aaaService = (IAAAService) wfms.getService(IAAAService.class);
		boolean ret = aaaService.authenticate(client);
		if ((ret) && (!userActivities.containsKey(client.getUserName())))
			userActivities.put(client.getUserName(), new HashSet());
		return ret;
	}
	
	/**
	 * Deauthenticate a client.
	 * @param client the client
	 */
	public synchronized void deauthenticate(IClient client)
	{
		IAAAService aaaService = (IAAAService) wfms.getService(IAAAService.class);
		workitemQueueListeners.remove(client);
		aaaService.deauthenticate(client);
	}
	
	/**
	 * Starts a new BPMN-process
	 * 
	 * @param client the client
	 * @param name name of the process
	 */
	public synchronized void startBpmnProcess(IClient client, String name)
	{
		if(!((IAAAService)wfms.getService(IAAAService.class)).accessAction(client, IAAAService.START_BPMN_PROCESS))
			throw new AccessControlException("Not allowed: "+client);
		
		IModelRepositoryService rs = (IModelRepositoryService)wfms.getService(IModelRepositoryService.class);
		String filename = rs.getProcessModel(name).getFilename();
		
		IExecutionService bps = (IExecutionService)wfms.getService(IExecutionService.class);
		Object id  = bps.startProcess(filename, null, null, false);
		System.out.println("Started process instance " + id);
	}
	
	/**
	 * Gets the names of all available BPMN-models
	 * 
	 * @param client the client
	 * @return the names of all available BPMN-models
	 */
	public synchronized Set getBpmnModelNames(IClient client)
	{
		if(!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_MODEL_NAMES))
			throw new AccessControlException("Not allowed: "+client);
		
		IModelRepositoryService rs = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		return new HashSet(rs.getModelNames());
	}
	
	/**
	 * Gets the names of all available BPMN-models
	 * 
	 * @param client the client
	 * @return the names of all available BPMN-models
	 */
	public synchronized Set getModelNames(IClient client)
	{
		if(!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_MODEL_NAMES))
			throw new AccessControlException("Not allowed: "+client);
		
		IModelRepositoryService rs = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		return new HashSet(rs.getModelNames());
	}
	
	/**
	 * Starts a new GPMN-process
	 * 
	 * @param client the client
	 * @param name name of the process
	 */
	public synchronized void startProcess(IClient client, String name)
	{
		if(!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.START_BPMN_PROCESS))
			throw new AccessControlException("Not allowed: "+client);
		
		IModelRepositoryService rs = (IModelRepositoryService)wfms.getService(IModelRepositoryService.class);
		String filename = rs.getProcessModel(name).getFilename();
		
		IExecutionService bps = (IExecutionService)wfms.getService(IExecutionService.class);
		Object id  = bps.startProcess(filename, null, null, false);
		System.out.println("Started process instance " + id);
	}
	
	/**
	 *  Finishes an Activity.
	 *  @param client the client
	 *  @param workitem the activity being finished
	 */
	public synchronized void finishActivity(IClient client, IClientActivity activity)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.COMMIT_WORKITEM))
			throw new AccessControlException("Not allowed: "+client);
		IResultListener listener = (IResultListener) processWorkitemListeners.remove(activity);
		((HashSet) userActivities.get(client.getUserName())).remove(activity);
		fireActivityRemovedEvent(client, activity);
		listener.resultAvailable(this, activity);
	}
	
	/**
	 *  Begins an activity for a client.
	 *  @param client the client
	 *  @param workitem the workitem being requested for the activity
	 */
	public synchronized void beginActivity(IClient client, IWorkitem workitem)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.ACQUIRE_WORKITEM))
			throw new AccessControlException("Not allowed: "+client);
		Set workitems = (Set) workitemQueues.get(workitem.getRole());
		if (workitems.remove(workitem))
		{
			fireWorkitemRemovedEvent(workitem);
			((HashSet) userActivities.get(client.getUserName())).add(workitem);
			fireActivityAddedEvent(client, (IClientActivity) workitem);
		}
	}
	
	/**
	 *  Cancel an activity.
	 *  @param client the client
	 *  @param activity the activity being canceled
	 */
	public void cancelActivity(IClient client, IClientActivity activity)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.RELEASE_WORKITEM))
			throw new AccessControlException("Not allowed: "+client);
		((HashSet) userActivities.get(client.getUserName())).remove(activity);
		fireActivityRemovedEvent(client, activity);
		queueWorkitem((IWorkitem) activity, null);
	}
	
	public synchronized Set getAvailableWorkitems(IClient client)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_AVAILABLE_WORKITEMS))
			throw new AccessControlException("Not allowed: "+client);
		IAAAService roleService = (IAAAService) wfms.getService(IAAAService.class);
		Set roles = roleService.getRoles(client);
		Set workitems = new HashSet();
		if (roles.contains(IAAAService.ALL_ROLES))
		{
			for (Iterator it = workitemQueues.values().iterator(); it.hasNext(); )
			{
				Set roleItems = (Set) it.next();
				workitems.addAll(roleItems);
			}
		}
		else
		{
			for (Iterator it = roles.iterator(); it.hasNext(); )
			{
				Set roleItems = (Set) workitemQueues.get(it.next());
				if (roleItems != null)
					workitems.addAll(roleItems);
			}
		}
		return workitems;
	}
	
	/**
	 *  Returns all activities available to a client.
	 *  @param client the client
	 *  @return a set of activities that are available for this client
	 */
	public synchronized Set getAvailableActivities(IClient client)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_AVAILABLE_ACTIVITIES))
			throw new AccessControlException("Not allowed: "+client);
		return new HashSet((Set) userActivities.get(client.getUserName()));
	}
	
	/**
	 *  Adds a listener for workitem queue changes and other WFMS changes.
	 *  @param listener a new WFMS listener
	 */
	public synchronized void addWfmsListener(IWorkitemListener listener)
	{
		workitemQueueListeners.put(listener.getClient(), listener);
		Set workitems = getAvailableWorkitems(listener.getClient());
		if (workitems != null)
		{
			for (Iterator it = workitems.iterator(); it.hasNext(); )
			{
				listener.workitemAdded(new WorkitemEvent((IWorkitem) it.next()));
			}
		}
	}
	
	/**
	 *  Removes a listener for workitem queue changes and other WFMS changes.
	 *  @param listener a new WFMS listener
	 */
	public synchronized void removeWfmsListener(IWorkitemListener listener)
	{
		workitemQueueListeners.remove(listener.getClient());
	}
	
	private synchronized void fireWorkitemAddedEvent(IWorkitem workitem)
	{
		IAAAService as = (IAAAService) wfms.getService(IAAAService.class);
		for (Iterator it = workitemQueueListeners.values().iterator(); it.hasNext(); )
		{
			IWorkitemListener listener = (IWorkitemListener) it.next();
			WorkitemEvent evt = new WorkitemEvent(workitem);
			
			if (as.accessEvent(listener.getClient(), evt))
				listener.workitemAdded(evt);
		}
	}
	
	private synchronized void fireWorkitemRemovedEvent(IWorkitem workitem)
	{
		for (Iterator it = workitemQueueListeners.values().iterator(); it.hasNext(); )
		{
			IWorkitemListener listener = (IWorkitemListener) it.next();
			IAAAService as = (IAAAService) wfms.getService(IAAAService.class);
			WorkitemEvent evt = new WorkitemEvent(workitem);
			if (as.accessEvent(listener.getClient(), evt))
				listener.workitemRemoved(evt);
		}
	}
	
	private synchronized void fireActivityAddedEvent(IClient client, IClientActivity activity)
	{
		IAAAService as = (IAAAService) wfms.getService(IAAAService.class);
		Set clients = as.getAuthenticatedClients(client.getUserName());
		
		for (Iterator it = clients.iterator(); it.hasNext(); )
		{
			IClient currentClient = (IClient) it.next();
			IWorkitemListener listener = (IWorkitemListener) workitemQueueListeners.get(currentClient);
			ActivityEvent evt = new ActivityEvent(activity);
			
			if (as.accessEvent(listener.getClient(), evt))
				listener.activityAdded(evt);
		}
	}
	
	private synchronized void fireActivityRemovedEvent(IClient client, IClientActivity activity)
	{
		IAAAService as = (IAAAService) wfms.getService(IAAAService.class);
		Set clients = as.getAuthenticatedClients(client.getUserName());
		
		for (Iterator it = clients.iterator(); it.hasNext(); )
		{
			IClient currentClient = (IClient) it.next();
			IWorkitemListener listener = (IWorkitemListener) workitemQueueListeners.get(currentClient);
			ActivityEvent evt = new ActivityEvent(activity);
			
			if (as.accessEvent(listener.getClient(), evt))
				listener.activityRemoved(evt);
		}
	}
}
