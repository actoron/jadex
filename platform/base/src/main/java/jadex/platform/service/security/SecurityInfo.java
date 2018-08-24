package jadex.platform.service.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import jadex.bridge.service.types.security.ISecurityInfo;

/**
 *  Security meta-information of a message;
 *
 */
public class SecurityInfo implements ISecurityInfo
{
	/** Flag if the platform is an admin platform. */
	protected boolean adminplatform;
	
	/** Flag if the platform has a trusted name. */
	protected boolean trustedplatform;
	
	/** Platform name if authenticated. */
	protected String platformname;
	
	/** Networks containing the sender. */
	protected Set<String> networks;
	
	/** Roles of the sender. */
	protected Set<String> roles;
	
	/**
	 *  Creates the infos.
	 */
	public SecurityInfo()
	{
	}
	
	/**
	 *  Checks if the sender platform is authenticated.
	 *
	 *  @return True if authenticated.
	 */
	public boolean isAuthenticated()
	{
		return trustedplatform || adminplatform || (networks != null && networks.size() > 0);
	}
	
	/**
	 *  Gets if the sender is authenticated.
	 *
	 *  @return True if authenticated.
	 */
//	public boolean isPlatformAuthenticated()
//	{
//		return platformauth;
//	}
	
	/**
	 *  Sets if the sender is authenticated.
	 *
	 *  @param platformauth True if authenticated.
	 */
//	public void setPlatformAuthenticated(boolean platformauth)
//	{
//		this.platformauth = platformauth;
//	}
	
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
	public boolean isAdminPlatform()
	{
		return adminplatform;
	}

	/**
	 *  Sets the ID of the sender platform if it is trusted, null otherwise.
	 *
	 *  @param trustedplatform The ID of the sender platform if it is trusted, null otherwise.
	 */
	public void setAdminPlatform(boolean adminplatform)
	{
		this.adminplatform = adminplatform;
	}
	
	/**
	 *  Checks if the sender platform name is authenticated and trusted.
	 *
	 *  @return True, if trusted.
	 */
	public boolean isTrustedPlatform()
	{
		return trustedplatform;
	}

	/**
	 *  Sets if the sender platform name is authenticated and trusted.
	 *
	 *  @param trustedplatform True, if trusted.
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
	public Set<String> getNetworks()
	{
		return networks;
	}

	/**
	 *  Sets the networks.
	 *
	 *  @param networks The networks.
	 */
	public void setNetworks(Set<String> networks)
	{
		this.networks = Collections.unmodifiableSet(networks);
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
		this.roles = Collections.unmodifiableSet(roles);
	}

	/**
	 *  Convert to string.
	 */
	public String toString()
	{
		return "Authenticated: " + isAuthenticated() + ", Trusted: " + trustedplatform + ", Admin: " + adminplatform + ", Networks: " + Arrays.toString(networks.toArray()); 
	}
}
