package jadex.wfms.service.impl;

import jadex.wfms.IWfms;
import jadex.wfms.client.IClient;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.client.IWorkitemListener;
import jadex.wfms.client.Workitem;
import jadex.wfms.client.WorkitemQueueChangeEvent;
import jadex.wfms.service.IRoleService;
import jadex.wfms.service.IWfmsClientAccess;
import jadex.wfms.service.IWorkitemQueueService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class ClientConnector implements IWfmsClientAccess, IWorkitemQueueService
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
	
	public synchronized void commitWorkitem(IClient client, IWorkitem workitem)
	{
		((Workitem) workitem).getListener().resultAvailable(null);
	}
	
	public synchronized boolean acquireWorkitem(IClient client, IWorkitem workitem)
	{
		Set workitems = (Set) workitemQueues.get(workitem.getRole());
		boolean ret = workitems.remove(workitem);
		if (ret)
			fireWorkitemRemovedEvent(workitem);
		return ret;
	}
	
	public synchronized void releaseWorkitem(IClient client, IWorkitem workitem)
	{
		queueWorkitem(workitem);
	}
	
	public synchronized Set getAvailableWorkitems(IClient client)
	{
		IRoleService roleService = (IRoleService) wfms.getService(IRoleService.class);
		Set roles = roleService.getRoles(client.getUserName());
		Set workitems = new HashSet();
		for (Iterator it = roles.iterator(); it.hasNext(); )
		{
			Set roleItems = (Set) workitemQueues.get(it.next());
			if (roleItems != null)
				workitems.addAll(roleItems);
		}
		return workitems;
	}
	
	public synchronized void addWorkitemListener(IWorkitemListener listener)
	{
		workitemListeners.add(listener);
	}
	
	public synchronized void removeWorkitemListener(IWorkitemListener listener)
	{
		workitemListeners.remove(listener);
	}
	
	private synchronized void fireWorkitemAddedEvent(IWorkitem workitem)
	{
		for (Iterator it = workitemListeners.iterator(); it.hasNext(); )
		{
			IWorkitemListener listener = (IWorkitemListener) it.next();
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
