package jadex.wfms.service.impl;

import jadex.commons.concurrent.IResultListener;
import jadex.wfms.client.IClient;
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
	private Set authenticatedClients;
	
	private Map userroles;
	
	public static IAAAService getTestService()
	{
		Map userroles = new HashMap();
		Set roles = new HashSet();
		roles.add("All");
		userroles.put("TestUser", roles);
		return new BasicAAAService(userroles);
	}
	
	public BasicAAAService(Map userroles)
	{
		this.userroles = userroles!=null? userroles: new HashMap();
		authenticatedClients = new HashSet();
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
	 * Authenticated a new client.
	 * @param client the new client
	 * @return true, if the client has been successfully authenticated.
	 */
	public boolean authenticate(IClient client)
	{
		authenticatedClients.add(client);
		return true;
	}
	
	/**
	 * Checks if a client can access an action
	 * @param client the client requesting the action
	 * @param action the action the client is requesting
	 * @return true, if the client is authorized to perform the action, false otherwise
	 */
	public boolean accessAction(IClient client, int action)
	{
		if (authenticatedClients.contains(client))
			return true;
		return false;
	}
	
	/**
	 * Checks if a client can access an event
	 * @param client the client scheduled to receive the event
	 * @param event the event
	 * @return true, if the client is authorized to perform the action, false otherwise
	 */
	public boolean accessEvent(IClient client, Object event)
	{
		return true;
		/*if (!authenticatedClients.contains(client))
			return false;
		if (event instanceof WorkitemQueueChangeEvent)
		{
			WorkitemQueueChangeEvent evt = (WorkitemQueueChangeEvent) event;
			Set roles = getRoles(client);
			if ((roles.contains(evt.getWorkitem().getRole())) || (roles.contains(IAAAService.ALL_ROLES)))
				return true;
		}
		return false;*/
	}
	
	/**
	 * Adds a new user to the service.
	 * @param userName user name of the new user
	 * @param roles roles of the user
	 */
	public synchronized void addUser(String userName, Set roles)
	{
		userroles.put(userName, roles);
	}
	
	/**
	 * Removes a user from the service.
	 * @param userName user name of the user
	 */
	public synchronized void removeUser(String userName)
	{
		userroles.remove(userName);
	}
	
	public Set getRoles(IClient client)
	{
		String userName = client.getUserName();
		Set roles = (Set) userroles.get(userName);
		if (roles == null)
		{
			roles = new HashSet();
			roles.add(IAAAService.ALL_ROLES);
		}
		return roles;
	}
}
