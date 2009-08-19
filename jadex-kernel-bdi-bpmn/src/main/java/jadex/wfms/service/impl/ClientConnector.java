package jadex.wfms.service.impl;

import jadex.wfms.IWfms;
import jadex.wfms.client.IClient;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.client.IWorkitemListener;
import jadex.wfms.client.Workitem;
import jadex.wfms.client.WorkitemQueueChangeEvent;
import jadex.wfms.service.IAuthenticationService;
import jadex.wfms.service.IBpmnProcessService;
import jadex.wfms.service.IModelRepositoryService;
import jadex.wfms.service.IRoleService;
import jadex.wfms.service.IWfmsClientService;
import jadex.wfms.service.IWorkitemQueueService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class ClientConnector implements IWfmsClientService, IWorkitemQueueService
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
	 * @param name name of the process
	 */
	public void startBpmnProcess(String name)
	{
		IBpmnProcessService bps = (IBpmnProcessService) wfms.getService(IBpmnProcessService.class);
		String instanceName = bps.startProcess(name, false);
		System.out.println("Started process instance " + instanceName);
	}
	
	/**
	 * Gets the names of all available BPMN-models
	 * 
	 * @return the names of all available BPMN-models
	 */
	public Set getBpmnModelNames()
	{
		IModelRepositoryService rs = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		return rs.getBpmnModelNames();
	}
	
	public synchronized void commitWorkitem(IClient client, IWorkitem workitem)
	{
		Workitem wi = (Workitem) workitem;
		assert wi.isAcquired();
		wi.setAcquired(false);
		wi.getListener().resultAvailable(null);
	}
	
	public synchronized boolean acquireWorkitem(IClient client, IWorkitem workitem)
	{
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
		assert ((Workitem) workitem).isAcquired();
		((Workitem) workitem).setAcquired(false);
		queueWorkitem(workitem);
	}
	
	public synchronized Set getAvailableWorkitems(IClient client)
	{
		IRoleService roleService = (IRoleService) wfms.getService(IRoleService.class);
		Set roles = roleService.getRoles(client.getUserName());
		Set workitems = new HashSet();
		if (roles.contains(IRoleService.ALL_ROLES))
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
		IRoleService rs = (IRoleService) wfms.getService(IRoleService.class);
		for (Iterator it = workitemListeners.iterator(); it.hasNext(); )
		{
			IWorkitemListener listener = (IWorkitemListener) it.next();
			Set roles = rs.getRoles(listener.getClient().getUserName());
			if ((roles.contains(workitem.getRole())) || (roles.contains(IRoleService.ALL_ROLES)))
				listener.workitemAdded(new WorkitemQueueChangeEvent(workitem));
		}
	}
	
	private synchronized void fireWorkitemRemovedEvent(IWorkitem workitem)
	{
		for (Iterator it = workitemListeners.iterator(); it.hasNext(); )
		{
			IWorkitemListener listener = (IWorkitemListener) it.next();
			listener.workitemRemoved(new WorkitemQueueChangeEvent(workitem));
		}
	}
}
