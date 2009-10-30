package jadex.wfms.service.client;

import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.wfms.client.IClient;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.client.IWorkitemListener;
import jadex.wfms.client.Workitem;
import jadex.wfms.client.WorkitemQueueChangeEvent;
import jadex.wfms.service.definition.IProcessDefinitionService;
import jadex.wfms.service.repository.IModelRepositoryService;
import jadex.wfms.service.security.IAAAService;

import java.security.AccessControlException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 
 */
public class ClientConnector implements IClientService, IWorkitemQueueService
{
	
	private IServiceContainer container;
	
	private Map workitemQueues;
	
	private Set workitemListeners;
	
	public ClientConnector(IServiceContainer container)
	{
		this.container = container;
		workitemQueues = new HashMap();
		workitemListeners = new HashSet();
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
	public IProcessDefinitionService getProcessDefinitionService(IClient client)
	{
		if (!((IAAAService) container.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_PD_SERVICE))
			return null;
		
		return (IProcessDefinitionService) container.getService(IProcessDefinitionService.class);
	}
	
	/**
	 * Starts a new BPMN-process
	 * 
	 * @param client the client
	 * @param name name of the process
	 * /
	public synchronized void startBpmnProcess(IClient client, String name)
	{
		if(!((IAAAService)wfms.getService(IAAAService.class)).accessAction(client, IAAAService.START_BPMN_PROCESS))
			throw new AccessControlException("Not allowed: "+client);
		
		// todo:
		throw new UnsupportedOperationException();
		
//		IModelRepositoryService rs = (IModelRepositoryService)wfms.getService(IModelRepositoryService.class);
//		String filename = rs.getProcessModel(name).getFilename();
//		
//		IExecutionService bps = (IExecutionService)wfms.getService(IExecutionService.class);
//		Object id  = bps.startProcess(filename, null, null, false);
//		System.out.println("Started process instance " + id);
	}*/
	
	/**
	 * Gets the names of all available BPMN-models
	 * 
	 * @param client the client
	 * @return the names of all available BPMN-models
	 */
	public Set getBpmnModelNames(IClient client)
	{
		if(!((IAAAService) container.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_MODEL_NAMES))
			throw new AccessControlException("Not allowed: "+client);
		
		IModelRepositoryService rs = (IModelRepositoryService) container.getService(IModelRepositoryService.class);
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
		if(!((IAAAService) container.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_MODEL_NAMES))
			throw new AccessControlException("Not allowed: "+client);
		
		IModelRepositoryService rs = (IModelRepositoryService) container.getService(IModelRepositoryService.class);
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
		if(!((IAAAService) container.getService(IAAAService.class)).accessAction(client, IAAAService.START_BPMN_PROCESS))
			throw new AccessControlException("Not allowed: "+client);
		
		// todo:
		throw new UnsupportedOperationException();
		
//		IModelRepositoryService rs = (IModelRepositoryService)wfms.getService(IModelRepositoryService.class);
//		String filename = rs.getProcessModel(name).getFilename();
//		
//		IExecutionService bps = (IExecutionService)wfms.getService(IExecutionService.class);
//		Object id  = bps.startProcess(filename, null, null, false);
//		System.out.println("Started process instance " + id);
	}
	
	/**
	 *  Finishes an Activity.
	 *  @param client the client
	 *  @param workitem the activity being finished
	 */
	public synchronized void finishActivity(IClient client, IClientActivity activity)
	{
		if (!((IAAAService) container.getService(IAAAService.class)).accessAction(client, IAAAService.COMMIT_WORKITEM))
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
		if (!((IAAAService) container.getService(IAAAService.class)).accessAction(client, IAAAService.ACQUIRE_WORKITEM))
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
		if (!((IAAAService) container.getService(IAAAService.class)).accessAction(client, IAAAService.RELEASE_WORKITEM))
			return;
		queueWorkitem((IWorkitem) activity);
	}
	
	public synchronized Set getAvailableWorkitems(IClient client)
	{
		if (!((IAAAService) container.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_AVAILABLE_WORKITEMS))
			return null;
		IAAAService roleService = (IAAAService) container.getService(IAAAService.class);
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
	
	public synchronized void addWorkitemListener(IWorkitemListener listener)
	{
		workitemListeners.add(listener);
		Set workitems = getAvailableWorkitems(listener.getClient());
		for (Iterator it = workitems.iterator(); it.hasNext(); )
		{
			listener.workitemAdded(new WorkitemQueueChangeEvent((IWorkitem) it.next()));
		}
	}
	
	public synchronized void removeWorkitemListener(IWorkitemListener listener)
	{
		workitemListeners.remove(listener);
	}
	
	private synchronized void fireWorkitemAddedEvent(IWorkitem workitem)
	{
		IAAAService as = (IAAAService) container.getService(IAAAService.class);
		for (Iterator it = workitemListeners.iterator(); it.hasNext(); )
		{
			IWorkitemListener listener = (IWorkitemListener) it.next();
			WorkitemQueueChangeEvent evt = new WorkitemQueueChangeEvent(workitem);
			
			if (as.accessEvent(listener.getClient(), evt))
				listener.workitemAdded(evt);
		}
	}
	
	private synchronized void fireWorkitemRemovedEvent(IWorkitem workitem)
	{
		for (Iterator it = workitemListeners.iterator(); it.hasNext(); )
		{
			IWorkitemListener listener = (IWorkitemListener) it.next();
			IAAAService as = (IAAAService) container.getService(IAAAService.class);
			WorkitemQueueChangeEvent evt = new WorkitemQueueChangeEvent(workitem);
			if (as.accessEvent(listener.getClient(), evt))
				listener.workitemRemoved(evt);
		}
	}
}
