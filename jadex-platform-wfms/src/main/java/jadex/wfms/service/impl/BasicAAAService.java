package jadex.wfms.service.impl;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.IResultListener;
import jadex.service.BasicService;
import jadex.service.IService;
import jadex.service.IServiceContainer;
import jadex.service.IServiceProvider;
import jadex.wfms.bdi.client.standard.SCapReqs;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IAdministrationService;
import jadex.wfms.service.IAuthenticationListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/** 
 *	Basic Authentication, Access control and Accounting Service.
 */
public class BasicAAAService extends BasicService implements IAAAService
{
	private Map userClients;
	
	private Map users;
	
	private Map secRoleCaps;
	
	private Set authenticationListeners;
	
	public static IAAAService getTestService(IServiceContainer provider)
	{
		Map secRoles = new HashMap();
		Set userNoStartCaps = new HashSet();
		userNoStartCaps.addAll(SCapReqs.ACTIVITY_HANDLING);
		userNoStartCaps.addAll(SCapReqs.WORKITEM_LIST);
		Set userCaps = new HashSet(userNoStartCaps);
		userCaps.addAll(SCapReqs.PROCESS_LIST);
		secRoles.put("User", userCaps);
		secRoles.put("User_No_Start", userNoStartCaps);
		UserAAAEntry userNoStart = new UserAAAEntry("TestUserNoStart", new String[] {IAAAService.ALL_ROLES}, new String[] {"User_No_Start"});
		UserAAAEntry user = new UserAAAEntry("TestUser", new String[] {IAAAService.ALL_ROLES}, new String[] {"User"});
		UserAAAEntry admin = new UserAAAEntry("TestAdmin", new String[] {IAAAService.ALL_ROLES}, new String[] {"Administrator"});
		UserAAAEntry bankTeller = new UserAAAEntry("BankTellerUser", new String[] {"Bank Teller"}, new String[] {"User"});
		return new BasicAAAService(new UserAAAEntry[] { admin, user, userNoStart, bankTeller }, secRoles, provider);
	}
	
	public BasicAAAService(UserAAAEntry[] users, Map secrolecaps, IServiceProvider provider)
	{
		super(provider.getId(), IAAAService.class, null);
		//super(BasicService.createServiceIdentifier(provider.getId(), BasicAAAService.class));

		this.authenticationListeners = new HashSet();
		this.users = new HashMap();
		for (int i = 0; i < users.length; ++i)
			this.users.put(users[i].getUserName(), users[i]);
		
		this.secRoleCaps = secrolecaps!=null? secrolecaps: new HashMap();
		secRoleCaps.put(IAAAService.SEC_ROLE_NONE, new HashSet());
		secRoleCaps.put(IAAAService.SEC_ROLE_ADMIN, new HashSet(IAAAService.CAPABILITIES));
		
		userClients = new HashMap();
	}
	
	/**
	 * Authenticate a new client.
	 * @param client the new client
	 * @return true, if the client has been successfully authenticated.
	 */
	public synchronized boolean authenticate(IClient client)
	{
		if (!users.containsKey(client.getUserName()))
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
	public synchronized boolean accessAction(IClient client, Integer action)
	{
		if (((Set) userClients.get(client.getUserName())).contains(client))
		{
			if (getCapabilities(((UserAAAEntry) users.get(client.getUserName())).getSecRoles()).contains(action))
				return true;
		}
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
	 * @param user the new user entry
	 */
	public synchronized void addUser(UserAAAEntry user)
	{
		users.put(user.getUserName(), user);
	}
	
	/**
	 * Removes a user from the service.
	 * @param userName user name of the user
	 */
	public synchronized void removeUser(String userName)
	{
		users.remove(userName);
	}
	
	/**
	 * Returns the roles of a particular user
	 * @param userName the user name
	 * @return the roles of the client
	 */
	public synchronized Set getRoles(String userName)
	{
		Set roles = ((UserAAAEntry) users.get(userName)).getRoles();
		return roles;
	}
	
	/**
	 * Returns the security roles of the user
	 * @param userName the user name
	 * @return the security roles
	 */
	public synchronized Set getSecurityRole(String userName)
	{
		return new HashSet(((UserAAAEntry) users.get(userName)).getSecRoles());
	}
	
	/**
	 * Returns the capabilities of a security role
	 * @param secRole the security role
	 * @return the capabilities of the security role
	 */
	public synchronized Set getCapabilities(String secRole)
	{
		return new HashSet((Set) secRoleCaps.get(secRole));
	}
	
	/**
	 * Returns the capabilities a set of security roles
	 * @param secRoles the security roles
	 * @return the combined capabilities of the security roles
	 */
	public synchronized Set getCapabilities(Set secRoles)
	{
		HashSet caps = new HashSet();
		for (Iterator it = secRoles.iterator(); it.hasNext(); )
			caps.addAll((Set) secRoleCaps.get(it.next()));
		
		return caps;
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
