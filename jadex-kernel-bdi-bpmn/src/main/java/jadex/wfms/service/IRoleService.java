package jadex.wfms.service;

import jadex.bridge.IPlatformService;

import java.util.Set;

/** 
 * Service providing access to the role system.
 */
public interface IRoleService extends IPlatformService
{
	public static final String ALL_ROLES = "all";
	
	/**
	 * Returns the roles of a particular user
	 * @param userName identifier of the user
	 * @return the roles of the user
	 */
	public Set getRoles(String userName);
}
