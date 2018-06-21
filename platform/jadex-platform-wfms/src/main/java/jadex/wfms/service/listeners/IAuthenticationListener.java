package jadex.wfms.service.listeners;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.IRemotable;
import jadex.commons.future.IFuture;
import jadex.wfms.client.ClientInfo;

/**
 * An authentication listener which triggers on
 * authentications and deauthentications.
 */
public interface IAuthenticationListener extends IRemotable
{
	/**
	 * This method triggers when a client has been authenticated.
	 * @param client client which has been authenticated
	 */
	public IFuture authenticated(IComponentIdentifier client, ClientInfo info);
	
	/**
	 * This method triggers when a client has been deauthenticated.
	 * @param client client which has been deauthenticated
	 */
	public IFuture deauthenticated(IComponentIdentifier client, ClientInfo info);
}
