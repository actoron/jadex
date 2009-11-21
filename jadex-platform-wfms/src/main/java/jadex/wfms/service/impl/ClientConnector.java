package jadex.wfms.service.impl;

import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.wfms.client.IClient;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWfmsListener;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.client.ProcessFinishedEvent;
import jadex.wfms.client.Workitem;
import jadex.wfms.client.WorkitemQueueChangeEvent;
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
	
	private Set wfmsListeners;
	
	public ClientConnector(IServiceContainer wfms)
	{
		this.wfms = wfms;
		workitemQueues = new HashMap();
		wfmsListeners = new HashSet();
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
	
	public synchronized void queueWorkitem(IWorkitem workitem)
	{
		Set workitems = (Set) workitemQueues.get(workitem.getRole());
		if (workitems == null)
		{
			workitems = new HashSet();
			workitemQueues.put(workitem.getRole(), workitems);
		}
		workitems.add(workitem);
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
	public boolean authenticate(IClient client)
	{
		IAAAService aaaService = (IAAAService) wfms.getService(IAAAService.class);
		return aaaService.authenticate(client);
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
	public Set getBpmnModelNames(IClient client)
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
	public Set getModelNames(IClient client)
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
			return;
		((Workitem) activity).getListener().resultAvailable(null);
	}
	
	/**
	 *  Begins an activity for a client.
	 *  @param client the client
	 *  @param workitem the workitem being requested for the activity
	 *  @return the corresponding activity, if the acquisition was successful, null otherwise
	 */
	public synchronized IClientActivity beginActivity(IClient client, IWorkitem workitem)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.ACQUIRE_WORKITEM))
			return null;
		Set workitems = (Set) workitemQueues.get(workitem.getRole());
		if (workitems.remove(workitem))
		{
			fireWorkitemRemovedEvent(workitem);
			return (IClientActivity) workitem;
		}
		return null;
	}
	
	/**
	 *  Cancel an activity.
	 *  @param client the client
	 *  @param activity the activity being canceled
	 */
	public void cancelActivity(IClient client, IClientActivity activity)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.RELEASE_WORKITEM))
			return;
		queueWorkitem((IWorkitem) activity);
	}
	
	public synchronized Set getAvailableWorkitems(IClient client)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_AVAILABLE_WORKITEMS))
			return null;
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
	 *  Adds a listener for workitem queue changes and other WFMS changes.
	 *  @param listener a new WFMS listener
	 */
	public void addWfmsListener(IWfmsListener listener)
	{
		wfmsListeners.add(listener);
		Set workitems = getAvailableWorkitems(listener.getClient());
		if (workitems != null)
		{
			for (Iterator it = workitems.iterator(); it.hasNext(); )
			{
				listener.workitemAdded(new WorkitemQueueChangeEvent((IWorkitem) it.next()));
			}
		}
	}
	
	/**
	 *  Removes a listener for workitem queue changes and other WFMS changes.
	 *  @param listener a new WFMS listener
	 */
	public void removeWfmsListener(IWfmsListener listener)
	{
		wfmsListeners.remove(listener);
	}
	
	private synchronized void fireWorkitemAddedEvent(IWorkitem workitem)
	{
		IAAAService as = (IAAAService) wfms.getService(IAAAService.class);
		for (Iterator it = wfmsListeners.iterator(); it.hasNext(); )
		{
			IWfmsListener listener = (IWfmsListener) it.next();
			WorkitemQueueChangeEvent evt = new WorkitemQueueChangeEvent(workitem);
			
			if (as.accessEvent(listener.getClient(), evt))
				listener.workitemAdded(evt);
		}
	}
	
	private synchronized void fireWorkitemRemovedEvent(IWorkitem workitem)
	{
		for (Iterator it = wfmsListeners.iterator(); it.hasNext(); )
		{
			IWfmsListener listener = (IWfmsListener) it.next();
			IAAAService as = (IAAAService) wfms.getService(IAAAService.class);
			WorkitemQueueChangeEvent evt = new WorkitemQueueChangeEvent(workitem);
			if (as.accessEvent(listener.getClient(), evt))
				listener.workitemRemoved(evt);
		}
	}
	
	public synchronized void fireProcessFinished(Object id)
	{
		for (Iterator it = wfmsListeners.iterator(); it.hasNext(); )
		{
			IWfmsListener listener = (IWfmsListener) it.next();
			IAAAService as = (IAAAService) wfms.getService(IAAAService.class);
			ProcessFinishedEvent evt = new ProcessFinishedEvent(String.valueOf(id));
			if (as.accessEvent(listener.getClient(), evt))
				listener.processFinished(evt);
		}
	}
}
