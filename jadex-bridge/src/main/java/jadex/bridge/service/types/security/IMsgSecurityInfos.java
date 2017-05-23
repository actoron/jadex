package jadex.bridge.service.types.security;

/**
 *  Security meta-information of a message;
 *
 */
public interface IMsgSecurityInfos
{
	/**
	 *  Checks if the sender platform ID is authenticated.
	 *
	 *  @return True if authenticated.
	 */
	public boolean isAuthenticatedPlatform();
	
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
}
