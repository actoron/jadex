package wfms.service.impl;

import wfms.service.IAuthenticationService;
import jadex.commons.concurrent.IResultListener;

/**
 * Authentication service that accepts anyone.
 *
 */
public class NullAccountingService implements IAuthenticationService
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
