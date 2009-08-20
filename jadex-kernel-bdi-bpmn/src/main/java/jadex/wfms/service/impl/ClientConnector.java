package jadex.wfms.service.impl;

import jadex.wfms.IWfms;
import jadex.wfms.client.IClient;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.client.IWorkitemListener;
import jadex.wfms.client.Workitem;
import jadex.wfms.client.WorkitemQueueChangeEvent;
import jadex.wfms.service.IBpmnProcessService;
import jadex.wfms.service.IGpmnProcessService;
import jadex.wfms.service.IModelRepositoryService;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IClientService;
import jadex.wfms.service.IWorkitemQueueService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class ClientConnector implements IClientService, IWorkitemQueueService
{
	
	private IWfms wfms;
	
	private Map workitemQueues;
	
	private Set workitemListeners;
	
	public ClientConnector(IWfms wfms)
	{
		this.wfms = wfms;
		workitemQueues = new HashMap();
		workitemListeners = new HashSet();
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
	 * Starts a new BPMN-process
	 * 
	 * @param client the client
	 * @param name name of the process
	 */
	public void startBpmnProcess(IClient client, String name)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.START_BPMN_PROCESS))
			return;
		IBpmnProcessService bps = (IBpmnProcessService) wfms.getService(IBpmnProcessService.class);
		String instanceName = bps.startProcess(name, false);
		System.out.println("Started process instance " + instanceName);
	}
	
	/**
	 * Gets the names of all available BPMN-models
	 * 
	 * @param client the client
	 * @return the names of all available BPMN-models
	 */
	public Set getBpmnModelNames(IClient client)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_BPMN_MODEL_NAMES))
			return null;
		IModelRepositoryService rs = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		return rs.getBpmnModelNames();
	}
	
	/**
	 * Starts a new GPMN-process
	 * 
	 * @param client the client
	 * @param name name of the process
	 */
	public void startGpmnProcess(IClient client, String name)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.START_GPMN_PROCESS))
			return;
		IGpmnProcessService gps = (IGpmnProcessService) wfms.getService(IGpmnProcessService.class);
		String instanceName = gps.startProcess(name);
		System.out.println("Started process instance " + instanceName);
	}
	
	/**
	 * Gets the names of all available GPMN-models
	 * 
	 * @param client the client
	 * @return the names of all available GPMN-models
	 */
	public Set getGpmnModelNames(IClient client)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_GPMN_MODEL_NAMES))
			return null;
		IModelRepositoryService rs = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		return rs.getGpmnModelNames();
	}
	
	public synchronized void commitWorkitem(IClient client, IWorkitem workitem)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.COMMIT_WORKITEM))
			return;
		Workitem wi = (Workitem) workitem;
		assert wi.isAcquired();
		wi.setAcquired(false);
		wi.getListener().resultAvailable(null);
	}
	
	public synchronized boolean acquireWorkitem(IClient client, IWorkitem workitem)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.ACQUIRE_WORKITEM))
			return false;
		Set workitems = (Set) workitemQueues.get(workitem.getRole());
		boolean ret = workitems.remove(workitem);
		if (ret)
		{
			fireWorkitemRemovedEvent(workitem);
			((Workitem) workitem).setAcquired(true);
		}
		return ret;
	}
	
	public synchronized void releaseWorkitem(IClient client, IWorkitem workitem)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.RELEASE_WORKITEM))
			return;
		assert ((Workitem) workitem).isAcquired();
		((Workitem) workitem).setAcquired(false);
		queueWorkitem(workitem);
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
		IAAAService as = (IAAAService) wfms.getService(IAAAService.class);
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
			IAAAService as = (IAAAService) wfms.getService(IAAAService.class);
			WorkitemQueueChangeEvent evt = new WorkitemQueueChangeEvent(workitem);
			if (as.accessEvent(listener.getClient(), evt))
				listener.workitemRemoved(evt);
		}
	}
}
