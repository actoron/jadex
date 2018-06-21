package jadex.bridge.service.types.security;

import java.util.Set;

/**
 *  Security meta-information of a message;
 *
 */
public interface IMsgSecurityInfos
{
	/**
	 *  Checks if the platform has any authentication.
	 *
	 *  @return True if authenticated.
	 */
	public boolean isAuthenticated();
	
	/**
	 *  Gets if the platform is authenticated.
	 *
	 *  @return True if authenticated.
	 */
	public boolean isPlatformAuthenticated();
	
	/**
	 *  Returns the authenticated platform name.
	 *
	 *  @return The authenticated platform name, null if not authenticated.
	 */
	public String getAuthenticatedPlatformName();
	
	/**
	 *  Gets the ID of the sender platform if it is trusted, null otherwise.
	 *
	 *  @return The ID of the sender platform if it is trusted, null otherwise.
	 */
	public boolean isTrustedPlatform();
	
	/**
	 *  Gets the authenticated networks of the sender.
	 *
	 *  @return The authenticated networks of the sender (sorted).
	 */
	public String[] getNetworks();
	
	/**
	 *  Gets the roles associated with the sender.
	 *
	 *  @return The roles.
	 */
	public Set<String> getRoles();
}
