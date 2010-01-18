package jadex.wfms.service;

import jadex.wfms.client.IClient;

/**
 * An authentication listener which triggers on
 * authentications and deauthentications.
 */
public interface IAuthenticationListener
{
	/**
	 * This method triggers when a client has been authenticated.
	 * @param client client which has been authenticated
	 */
	public void authenticated(IClient client);
	
	/**
	 * This method triggers when a client has been deauthenticated.
	 * @param client client which has been deauthenticated
	 */
	public void deauthenticated(IClient client);
}
