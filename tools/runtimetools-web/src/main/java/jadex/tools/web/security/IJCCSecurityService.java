package jadex.tools.web.security;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.tools.web.jcc.IJCCPluginService;
import jadex.tools.web.security.JCCSecurityPluginAgent.SecurityState;

/**
 *  Interface for the jcc security service.
 */
@Service(system=true)
public interface IJCCSecurityService extends IJCCPluginService
{
	/**
	 *  Get security state.
	 *  @return The security state.
	 */
	public IFuture<SecurityState> getSecurityState();
	
	/**
	 *  Set if the platform secret shall be used.
	 *  @param usesecret The flag.
	 */
	public IFuture<Void> setUseSecret(boolean usesecret);
	
	/**
	 *  Set if the platform secret shall be printed.
	 *  @param printsecret The flag.
	 */
	public IFuture<Void> setPrintSecret(boolean printsecret);
	
	/**
	 *  Adds a role for an entity (platform or network name).
	 *  @param entity The entity name.
	 *  @param role The role name.
	 *  @return Null, when done.
	 */
	public IFuture<Void> addRole(String entity, String role);
	
	/**
	 *  Adds a role of an entity (platform or network name).
	 *  @param entity The entity name.
	 *  @param role The role name.
	 *  @return Null, when done.
	 */
	public IFuture<Void> removeRole(String entity, String role);
	
	/**
	 *  Adds a new network.
	 *  @param networkname The network name.
	 *  @param secret The secret, null to remove.
	 *  @return Null, when done.
	 */
	public IFuture<Void> addNetwork(String networkname, String secret);
	
	/**
	 *  Remove a network.
	 *  @param networkname The network name.
	 *  @param secret The secret, null to remove the network completely.
	 *  @return Null, when done.
	 */
	public IFuture<Void> removeNetwork(String networkname, String secret);
	
	/**
	 *  Adds a trusted platform name.
	 *  @param name The name.
	 *  @return null, when done.
	 */
	public IFuture<Void> addTrustedPlatformName(String name);
	
	/**
	 *  Removes a trusted platform name.
	 *  @param name The name.
	 *  @return null, when done.
	 */
	public IFuture<Void> removeTrustedPlatformName(String name);

}
