package jadex.bridge.service.types.security;

import java.util.Set;

/**
 *  Security meta-information of a message;
 *
 */
public interface ISecurityInfo
{
	/**
	 *  Checks if the platform has any authentication.
	 *
	 *  @return True if authenticated.
	 */
	public boolean hasDefaultAuthorization();
	
	/**
	 *  Returns the authenticated platform name.
	 *
	 *  @return The authenticated platform name, null if not authenticated.
	 */
	public String getAuthenticatedPlatformName();
	
	/**
	 *  Checks if the sender platform name is authenticated and trusted.
	 *
	 *  @return True, if trusted.
	 */
	public boolean isTrustedPlatform();
	
	/**
	 *  Gets the ID of the sender platform if it is trusted, null otherwise.
	 *
	 *  @return The ID of the sender platform if it is trusted, null otherwise.
	 */
	public boolean isAdminPlatform();
	
	/**
	 *  Gets the authenticated networks of the sender.
	 *
	 *  @return The authenticated networks of the sender.
	 */
	public Set<String> getNetworks();
	
	/**
	 *  Gets the authenticated networks shared with the sender.
	 *
	 *  @return The authenticated networks shared with the sender.
	 */
	public Set<String> getSharedNetworks();
	
	/**
	 *  Gets the roles associated with the sender.
	 *
	 *  @return The roles.
	 */
	public Set<String> getRoles();
}
