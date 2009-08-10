package jadex.wfms.service.impl;

import jadex.commons.concurrent.IResultListener;
import jadex.wfms.service.IAuthenticationService;

/**
 * Authentication service that accepts anyone.
 *
 */
public class NullAuthenticationService implements IAuthenticationService
{
	public void start()
	{
	}
	
	public void shutdown(IResultListener listener)
	{
	}
	
	/**
	 * Authenticates the user with no check at all.
	 * @param userName name of the user
	 * @param authenticationToken proof of identity
	 * @return always true
	 */
	public boolean authenticateUser(String userName, Object authenticationToken)
	{
		return true;
	}
}
