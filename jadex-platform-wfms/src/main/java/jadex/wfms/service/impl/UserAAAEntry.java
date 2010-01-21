package jadex.wfms.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Entry for a user of the WfMS
 *
 */
public class UserAAAEntry
{
	/** The user name */
	private String userName;
	
	/** The roles of the user */
	private Set roles;
	
	/** The security roles of the user */
	private Set secRoles;
	
	/**
	 * Creates a new AAA Entry
	 * @param userName name of the user
	 * @param roles roles of the user
	 * @param secRoles security roles of the user
	 */
	public UserAAAEntry(String userName, String[] roles, String[] secRoles)
	{
		this.userName = userName;
		this.roles = new HashSet(Arrays.asList(roles));
		this.secRoles = new HashSet(Arrays.asList(secRoles));
	}
	
	/**
	 * Returns the user name
	 * @return the user name
	 */
	public String getUserName()
	{
		return userName;
	}
	
	/**
	 * Returns the roles of the user.
	 * @return roles of the user
	 */
	public Set getRoles()
	{
		return roles;
	}
	
	/**
	 * Returns the security roles of the user.
	 * @return security roles of the user
	 */
	public Set getSecRoles()
	{
		return secRoles;
	}
}
