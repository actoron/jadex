package jadex.wfms.service.security;

import jadex.commons.concurrent.IResultListener;
import jadex.wfms.client.IClient;
import jadex.wfms.client.WorkitemQueueChangeEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/** 
 *	Basic Authentication, Access control and Accounting Service.
 */
public class BasicAAAService implements IAAAService
{
	private Map userroles;
	
	public BasicAAAService()
	{
		this(null);
	}
	
	public BasicAAAService(Map userroles)
	{
		this.userroles = userroles!=null? userroles: new HashMap();
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
		userroles.put(userName, roles);
	}
	
	public synchronized void removeUser(String userName)
	{
		userroles.remove(userName);
	}
	
	public Set getRoles(IClient client)
	{
		String userName = client.getUserName();
		Set roles = (Set) userroles.get(userName);
		if (roles == null)
			roles = new HashSet();
		return roles;
	}
}
