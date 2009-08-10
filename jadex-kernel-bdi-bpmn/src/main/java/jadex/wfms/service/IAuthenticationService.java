package jadex.wfms.service;

import jadex.bridge.IPlatformService;

/**
 * User authentication service.
 */
public interface IAuthenticationService extends IPlatformService
{
	/**
	 * Attempts to authenticate a user
	 * @param userName name of the user
	 * @param authenticationToken proof of identity
	 * @return true, if the user has been authenticated, false otherwise
	 */
	public boolean authenticateUser(String userName, Object authenticationToken);
}
