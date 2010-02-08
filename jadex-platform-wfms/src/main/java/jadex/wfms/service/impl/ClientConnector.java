package jadex.wfms.service.impl;

import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;
import jadex.service.IServiceContainer;
import jadex.wfms.client.IClient;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.listeners.ActivityEvent;
import jadex.wfms.listeners.IActivityListener;
import jadex.wfms.listeners.IWorkitemListener;
import jadex.wfms.listeners.WorkitemEvent;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IAdministrationService;
import jadex.wfms.service.IClientService;
import jadex.wfms.service.IExecutionService;
import jadex.wfms.service.IModelRepositoryService;
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
public class ClientConnector implements IClientService, IWfmsClientService, IService
{
	
	private IServiceContainer wfms;
	
	private Map workitemQueues;
	
	private Map userActivities;
	
	private Map processWorkitemListeners;
	
	private Map workitemQueueListeners;
	
	private Map activityListeners;
	
	private Set wfmsActivityListeners;
	
	public ClientConnector(IServiceContainer wfms)
	{
		this.wfms = wfms;
		processWorkitemListeners = new HashMap();
		workitemQueues = new HashMap();
		userActivities = new HashMap();
		workitemQueueListeners = new HashMap();
		activityListeners = new HashMap();
		wfmsActivityListeners = new HashSet();
	}
	
	/**
	 *  Start the service.
	 */
	public void startService()
	{
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdownService(IResultListener listener)
	{
		if(listener!=null)
			listener.resultAvailable(this, null);
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
	 * Returns the current activities for all users
	 * 
	 * @return current activities for all users
	 */
	public synchronized Map getUserActivities()
	{
		Map ret = new HashMap();
		for (Iterator it = userActivities.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry userEntry = (Map.Entry) it.next();
			Set activities = (Set) userEntry.getValue();
			ret.put(userEntry.getKey(), new HashSet(activities));
		}
		
		return ret;
	}
	
	/**
	 * Terminates the activity of a user.
	 * 
	 * @param activity the activity
	 */
	public synchronized void terminateActivity(IClientActivity activity)
	{
		String userName = null;
		for (Iterator it = userActivities.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) it.next();
			Set activities = (Set) entry.getValue();
			if (activities.contains(activity))
			{
				userName = (String) entry.getKey();
				break;
			}
		}
		if (userName != null)
		{
			((Set) userActivities.get(userName)).remove(activity);
			fireActivityRemovedEvent(userName, activity);
			queueWorkitem((IWorkitem) activity, null);
		}
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
	public synchronized IAdministrationService getMonitoringService(IClient client)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_MONITORING_SERVICE))
			return null;
		return (IAdministrationService) wfms.getService(IAdministrationService.class);
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
		activityListeners.remove(client);
		aaaService.deauthenticate(client);
	}
	
	/**
	 * Returns the capabilities of the client
	 * @param client the client
	 * @return set of capabilities
	 */
	public Set getCapabilities(IClient client)
	{
		IAAAService aaaService = (IAAAService) wfms.getService(IAAAService.class);
		return aaaService.getCapabilities(aaaService.getSecurityRole(client.getUserName()));
	}
	
	/**
	 * Starts a new BPMN-process
	 * 
	 * @param client the client
	 * @param name name of the process
	 */
	/*public synchronized void startBpmnProcess(IClient client, String name)
	{
		if(!((IAAAService)wfms.getService(IAAAService.class)).accessAction(client, IAAAService.START_BPMN_PROCESS))
			throw new AccessControlException("Not allowed: "+client);
		
		IModelRepositoryService rs = (IModelRepositoryService)wfms.getService(IModelRepositoryService.class);
		String filename = rs.getProcessModel(name).getFilename();
		
		IExecutionService bps = (IExecutionService)wfms.getService(IExecutionService.class);
		Object id  = bps.startProcess(filename, null, null, false);
		System.out.println("Started process instance " + id);
	}*/
	
	/**
	 * Gets the names of all available BPMN-models
	 * 
	 * @param client the client
	 * @return the names of all available BPMN-models
	 */
	/*public synchronized Set getBpmnModelNames(IClient client)
	{
		if(!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_MODEL_NAMES))
			throw new AccessControlException("Not allowed: "+client);
		
		IModelRepositoryService rs = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		return new HashSet(rs.getModelNames());
	}*/
	
	/**
	 * Gets the names of all available process models
	 * 
	 * @param client the client
	 * @return the names of all available process models
	 */
	public synchronized Set getModelNames(IClient client)
	{
		if(!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.PD_REQUEST_MODEL_NAMES))
			throw new AccessControlException("Not allowed: "+client);
		
		IModelRepositoryService rs = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		return new HashSet(rs.getModelNames());
	}
	
	/**
	 * Starts a new process
	 * 
	 * @param client the client
	 * @param name name of the process
	 */
	public synchronized void startProcess(IClient client, String name)
	{
		if(!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.START_PROCESS))
			throw new AccessControlException("Not allowed: "+client);
		
		IModelRepositoryService rs = (IModelRepositoryService)wfms.getService(IModelRepositoryService.class);
		String filename = rs.getProcessModel(name).getFilename();
		
		IExecutionService bps = (IExecutionService)wfms.getService(IExecutionService.class);
		Object id  = bps.startProcess(filename, null, null, false);
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
		fireActivityRemovedEvent(client.getUserName(), activity);
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
			fireActivityAddedEvent(client.getUserName(), (IClientActivity) workitem);
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
		fireActivityRemovedEvent(client.getUserName(), activity);
		queueWorkitem((IWorkitem) activity, null);
	}
	
	public synchronized Set getAvailableWorkitems(IClient client)
	{
		IAAAService as = (IAAAService) wfms.getService(IAAAService.class);
		if ((!as.accessAction(client, IAAAService.REQUEST_AVAILABLE_WORKITEMS)) &&
			(!as.accessAction(client, IAAAService.ADD_WORKITEM_LISTENER)))
			throw new AccessControlException("Not allowed: "+client);
		
		IAAAService roleService = (IAAAService) wfms.getService(IAAAService.class);
		Set roles = roleService.getRoles(client.getUserName());
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
	 *  Adds a listener for workitem queue changes relevant to the client.
	 *  @param client the client
	 *  @param listener a new WFMS listener
	 */
	public synchronized void addWorkitemListener(IClient client, IWorkitemListener listener)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.ADD_WORKITEM_LISTENER))
			throw new AccessControlException("Not allowed: "+client);
		Set listeners = (Set) workitemQueueListeners.get(client);
		if (listeners == null)
		{
			listeners = new HashSet();
			workitemQueueListeners.put(client, listeners);
		}
		listeners.add(listener);
		Set workitems = getAvailableWorkitems(client);
		if (workitems != null)
		{
			for (Iterator it = workitems.iterator(); it.hasNext(); )
			{
				listener.workitemAdded(new WorkitemEvent((IWorkitem) it.next()));
			}
		}
	}
	
	/**
	 *  Removes a listener for workitem queue changes relevant to the client.
	 *  @param client the client
	 *  @param listener a new WFMS listener
	 */
	public synchronized void removeWorkitemListener(IClient client, IWorkitemListener listener)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REMOVE_WORKITEM_LISTENER))
			throw new AccessControlException("Not allowed: "+client);
		Set listeners = (Set) workitemQueueListeners.get(client);
		if (listeners != null)
			listeners.remove(listener);
	}
	
	/**
	 *  Adds a listener for activity changes.
	 *  @param listener a new activity listener
	 */
	public synchronized void addActivityListener(IActivityListener listener)
	{
		wfmsActivityListeners.add(listener);
		
		for (Iterator it = userActivities.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) it.next();
			String userName = (String) entry.getKey();
			Set activities = (Set) entry.getValue();
			for (Iterator it2 = activities.iterator(); it2.hasNext(); )
				listener.activityAdded(new ActivityEvent(userName, (IClientActivity) it2.next()));
		}
	}
	
	/**
	 *  Removes a listener for activity changes.
	 *  @param listener activity listener
	 */
	public synchronized void removeActivityListener(IActivityListener listener)
	{
		wfmsActivityListeners.remove(listener);
	}
	
	/**
	 *  Adds a listener for activity changes of the client.
	 *  @param client the client
	 *  @param listener a new activity listener
	 */
	public synchronized void addActivityListener(IClient client, IActivityListener listener)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.ADD_ACTIVITY_LISTENER))
			throw new AccessControlException("Not allowed: "+client);
		Set listeners = (Set) activityListeners.get(client);
		if (listeners == null)
		{
			listeners = new HashSet();
			activityListeners.put(client, listeners);
		}
		listeners.add(listener);
		Set activities = (Set) userActivities.get(client.getUserName());
		if (activities != null)
		{
			for (Iterator it = activities.iterator(); it.hasNext(); )
			{
				listener.activityAdded(new ActivityEvent(client.getUserName(), (IClientActivity) it.next()));
			}
		}
	}
	
	/**
	 *  Removes a listener for activity changes of the client.
	 *  @param client the client
	 *  @param listener a new activity listener
	 */
	public synchronized void removeActivityListener(IClient client, IActivityListener listener)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REMOVE_ACTIVITY_LISTENER))
			throw new AccessControlException("Not allowed: "+client);
		Set listeners = (Set) activityListeners.get(client);
		if (listeners != null)
			listeners.remove(listener);
	}
	
	private void fireWorkitemAddedEvent(IWorkitem workitem)
	{
		IAAAService as = (IAAAService) wfms.getService(IAAAService.class);
		for (Iterator it = workitemQueueListeners.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) it.next();
			IClient client = (IClient) entry.getKey();
			Set listeners = (Set) entry.getValue();
			
			if (as.getRoles(client.getUserName()).contains(workitem.getRole()) ||
				workitem.getRole().equals(IAAAService.ANY_ROLE) ||
				as.getRoles(client.getUserName()).contains(IAAAService.ALL_ROLES))
			{
				WorkitemEvent evt = new WorkitemEvent(workitem);
				for (Iterator it2 = listeners.iterator(); it2.hasNext(); )
					((IWorkitemListener) it2.next()).workitemAdded(evt);
			}
		}
	}
	
	private void fireWorkitemRemovedEvent(IWorkitem workitem)
	{
		IAAAService as = (IAAAService) wfms.getService(IAAAService.class);
		for (Iterator it = workitemQueueListeners.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) it.next();
			IClient client = (IClient) entry.getKey();
			Set listeners = (Set) entry.getValue();
			
			if (as.getRoles(client.getUserName()).contains(workitem.getRole()) ||
				workitem.getRole().equals(IAAAService.ANY_ROLE) ||
				as.getRoles(client.getUserName()).contains(IAAAService.ALL_ROLES))
			{
				WorkitemEvent evt = new WorkitemEvent(workitem);
				for (Iterator it2 = listeners.iterator(); it2.hasNext(); )
					((IWorkitemListener) it2.next()).workitemRemoved(evt);
			}
		}
	}
	
	private void fireActivityAddedEvent(String userName, IClientActivity activity)
	{
		IAAAService as = (IAAAService) wfms.getService(IAAAService.class);
		Set clients = as.getAuthenticatedClients(userName);
		
		for (Iterator it = clients.iterator(); it.hasNext(); )
		{
			IClient currentClient = (IClient) it.next();
			
			Set listeners = (Set) activityListeners.get(currentClient);
			
			for (Iterator it2 = listeners.iterator(); it2.hasNext(); )
			{
				IActivityListener listener = (IActivityListener) it2.next();
				ActivityEvent evt = new ActivityEvent(userName, activity);
				listener.activityAdded(evt);
			}
		}
		
		for (Iterator it = wfmsActivityListeners.iterator(); it.hasNext(); )
			((IActivityListener) it.next()).activityAdded(new ActivityEvent(userName, activity));
	}
	
	private void fireActivityRemovedEvent(String userName, IClientActivity activity)
	{
		IAAAService as = (IAAAService) wfms.getService(IAAAService.class);
		Set clients = as.getAuthenticatedClients(userName);
		
		for (Iterator it = clients.iterator(); it.hasNext(); )
		{
			IClient currentClient = (IClient) it.next();
			
			Set listeners = (Set) activityListeners.get(currentClient);
			
			for (Iterator it2 = listeners.iterator(); it2.hasNext(); )
			{
				IActivityListener listener = (IActivityListener) it2.next();
				ActivityEvent evt = new ActivityEvent(userName, activity);
				listener.activityRemoved(evt);
			}
		}
		
		for (Iterator it = wfmsActivityListeners.iterator(); it.hasNext(); )
			((IActivityListener) it.next()).activityRemoved(new ActivityEvent(userName, activity));
	}
}
