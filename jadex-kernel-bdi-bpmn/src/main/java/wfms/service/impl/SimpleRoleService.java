package wfms.service.impl;

import jadex.commons.concurrent.IResultListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import wfms.service.IRoleService;

/** A basic Role Service implementation.
 *
 */
public class SimpleRoleService implements IRoleService
{
	private Map userRoles;
	
	public void start()
	{
	}
	
	public void shutdown(IResultListener listener)
	{
	}
	
	public SimpleRoleService()
	{
		userRoles = new HashMap();
	}
	
	public synchronized void addUser(String userName, Set roles)
	{
		userRoles.put(userName, roles);
	}
	
	public synchronized void removeUser(String userName)
	{
		userRoles.remove(userName);
	}
	
	public Set getRoles(String userName)
	{
		Set roles = (Set) userRoles.get(userName);
		if (roles == null)
			roles = new HashSet();
		return roles;
	}
}
