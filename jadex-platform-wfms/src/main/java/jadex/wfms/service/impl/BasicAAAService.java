package jadex.wfms.service.impl;

import jadex.commons.concurrent.IResultListener;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IAuthenticationListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/** 
 *	Basic Authentication, Access control and Accounting Service.
 */
public class BasicAAAService implements IAAAService
{
	private Map userClients;
	
	private Map userroles;
	
	private Set authenticationListeners;
	
	public static IAAAService getTestService()
	{
		Map userroles = new HashMap();
		Set roles = new HashSet();
		roles.add(IAAAService.ALL_ROLES);
		userroles.put("TestUser", roles);
		return new BasicAAAService(userroles);
	}
	
	public BasicAAAService(Map userroles)
	{
		this.authenticationListeners = new HashSet();
		this.userroles = userroles!=null? userroles: new HashMap();
		userClients = new HashMap();
	}
	
	public BasicAAAService(String[] userNames, String[][] roles)
	{
		this.authenticationListeners = new HashSet();
		int min = Math.min(userNames.length, roles.length);
		userroles = new HashMap();
		for (int i = 0; i < min; ++i)
		{
			userroles.put(userNames[i], new HashSet(Arrays.asList(roles[i])));
		}
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
	 * Authenticate a new client.
	 * @param client the new client
	 * @return true, if the client has been successfully authenticated.
	 */
	public synchronized boolean authenticate(IClient client)
	{
		if (!userroles.containsKey(client.getUserName()))
			return false;
		if (!userClients.containsKey(client.getUserName()))
			userClients.put(client.getUserName(), Collections.synchronizedSet(new HashSet()));
		((Set) userClients.get(client.getUserName())).add(client);
		fireAuthenticationEvent(client);
		return true;
	}
	
	/**
	 * Deauthenticate a client.
	 * @param client the client
	 */
	public synchronized void deauthenticate(IClient client)
	{
		((Set) userClients.get(client.getUserName())).remove(client);
		fireDeauthenticationEvent(client);
	}
	
	/**
	 * Returns the authenticated clients for a specific user name
	 * @parameter userName the user name
	 * @return Set of connected clients
	 */
	public synchronized Set getAuthenticatedClients(String userName)
	{
		Set clients = (Set) userClients.get(userName);
		synchronized(clients)
		{
			clients = new HashSet(clients);
		}
		return clients;
	}
	
	/**
	 * Checks if a client can access an action
	 * @param client the client requesting the action
	 * @param action the action the client is requesting
	 * @return true, if the client is authorized to perform the action, false otherwise
	 */
	public synchronized boolean accessAction(IClient client, int action)
	{
		if (((Set) userClients.get(client.getUserName())).contains(client))
			return true;
		return false;
	}
	
	/**
	 * Checks if a client can access an event
	 * @param client the client scheduled to receive the event
	 * @param event the event
	 * @return true, if the client is authorized to perform the action, false otherwise
	 */
	public synchronized boolean accessEvent(IClient client, Object event)
	{
		return true;
		//TODO: FIXME
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
	
	public synchronized Set getRoles(IClient client)
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
	
	/**
	 * Adds an authentication listener which triggers on
	 * authentications and deauthentications.
	 * 
	 * @param listener the listener
	 */
	public synchronized void addAuthenticationListener(IAuthenticationListener listener)
	{
		authenticationListeners.add(listener);
	}
	
	/**
	 * Removes an authentication listener.
	 * 
	 * @param listener the listener
	 */
	public synchronized void removeAuthenticationListener(IAuthenticationListener listener)
	{
		authenticationListeners.remove(listener);
	}
	
	private void fireAuthenticationEvent(IClient client)
	{
		for (Iterator it = authenticationListeners.iterator(); it.hasNext(); )
			((IAuthenticationListener) it.next()).authenticated(client);
	}
	
	private void fireDeauthenticationEvent(IClient client)
	{
		for (Iterator it = authenticationListeners.iterator(); it.hasNext(); )
			((IAuthenticationListener) it.next()).deauthenticated(client);
	}
}
