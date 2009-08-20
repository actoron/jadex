package jadex.wfms.service.impl;

import jadex.commons.concurrent.IResultListener;
import jadex.wfms.client.IClient;
import jadex.wfms.client.WorkitemQueueChangeEvent;
import jadex.wfms.service.IAAAService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/** 
 *	Basic Authentication, Access control and Accounting Service.
 */
public class BasicAAAService implements IAAAService
{
	private Map userRoles;
	
	public BasicAAAService()
	{
		userRoles = new HashMap();
	}
	
	/**
	 * Checks if a client can access an action
	 * @param client the client requesting the action
	 * @param action the action the client is requesting
	 * @return true, if the client is authorized to perform the action, false otherwise
	 */
	public boolean accessAction(IClient client, int action)
	{
		return true;
	}
	
	/**
	 * Checks if a client can access an event
	 * @param client the client scheduled to receive the event
	 * @param event the event
	 * @return true, if the client is authorized to perform the action, false otherwise
	 */
	public boolean accessEvent(IClient client, Object event)
	{
		if (event instanceof WorkitemQueueChangeEvent)
		{
			WorkitemQueueChangeEvent evt = (WorkitemQueueChangeEvent) event;
			Set roles = getRoles(client);
			if ((roles.contains(evt.getWorkitem().getRole())) || (roles.contains(IAAAService.ALL_ROLES)))
				return true;
		}
		return false;
	}
	
	public synchronized void addUser(String userName, Set roles)
	{
		userRoles.put(userName, roles);
	}
	
	public synchronized void removeUser(String userName)
	{
		userRoles.remove(userName);
	}
	
	public Set getRoles(IClient client)
	{
		String userName = client.getUserName();
		Set roles = (Set) userRoles.get(userName);
		if (roles == null)
			roles = new HashSet();
		return roles;
	}
}
