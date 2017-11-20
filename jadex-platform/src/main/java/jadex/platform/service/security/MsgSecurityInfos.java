package jadex.platform.service.security;

import java.util.Arrays;
import java.util.Set;

import jadex.bridge.service.types.security.IMsgSecurityInfos;

/**
 *  Security meta-information of a message;
 *
 */
public class MsgSecurityInfos implements IMsgSecurityInfos
{
	/** Flag if the platform ID could be authenticated. */
	protected boolean auth;
	
	/** Flag if the platform is trusted. */
	protected boolean trustedplatform;
	
	/** Platform name if authenticated. */
	protected String platformname;
	
	/** Networks containing the sender. */
	protected String[] networks;
	
	/** Roles of the sender. */
	protected Set<String> roles;
	
	/**
	 *  Creates the infos.
	 */
	public MsgSecurityInfos()
	{
	}
	
	/**
	 *  Checks if the sender platform ID is authenticated.
	 *
	 *  @return True if authenticated.
	 */
	public boolean isAuthenticated()
	{
		return auth;
	}
	
	/**
	 *  Sets if the sender is authenticated.
	 *
	 *  @param authplatform True if authenticated.
	 */
	public void setAuthenticated(boolean auth)
	{
		this.auth = auth;
	}
	
	/**
	 *  Returns the authenticated platform name.
	 *
	 *  @return The authenticated platform name, null if not authenticated.
	 */
	public String getAuthenticatedPlatformName()
	{
		return platformname;
	}
	
	/**
	 *  Sets the authenticated platform name.
	 *
	 *  @param platformname The authenticated platform name, null if not authenticated.
	 */
	public void setAuthenticatedPlatformName(String platformname)
	{
		this.platformname = platformname;
	}

	/**
	 *  Checks if the sender platform is trusted.
	 *
	 *  @return True, if trusted.
	 */
	public boolean isTrustedPlatform()
	{
		return trustedplatform;
	}

	/**
	 *  Sets the ID of the sender platform if it is trusted, null otherwise.
	 *
	 *  @param trustedplatform The ID of the sender platform if it is trusted, null otherwise.
	 */
	public void setTrustedPlatform(boolean trustedplatform)
	{
		this.trustedplatform = trustedplatform;
	}

	/**
	 *  Gets the authenticated networks of the sender.
	 *
	 *  @return The authenticated networks of the sender (sorted).
	 */
	public String[] getNetworks()
	{
		return networks;
	}

	/**
	 *  Sets the networks.
	 *
	 *  @param networks The networks.
	 */
	public void setNetworks(String[] networks)
	{
		this.networks = networks;
	}
	
	/**
	 *  Gets the roles.
	 *
	 *  @return The roles.
	 */
	public Set<String> getRoles()
	{
		return roles;
	}

	/**
	 *  Sets the roles.
	 *
	 *  @param roles The roles.
	 */
	public void setRoles(Set<String> roles)
	{
		this.roles = roles;
	}

	/**
	 *  Convert to string.
	 */
	public String toString()
	{
		return "Authenticated: " + auth + ", Trusted: " + trustedplatform + ", Networks: " + Arrays.toString(networks); 
	}
}
