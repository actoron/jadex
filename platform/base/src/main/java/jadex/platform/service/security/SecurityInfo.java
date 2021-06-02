package jadex.platform.service.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.types.security.ISecurityInfo;

/**
 *  Security meta-information of a message;
 *
 */
public class SecurityInfo implements ISecurityInfo
{
	/** Flag if the platform is an admin platform. */
//	protected boolean adminplatform;
	
	/** Flag if the platform has a trusted name. */
//	protected boolean trustedplatform;
	
	/** Flag if default authorization is allowed. */
//	protected boolean allowdefaultauthorization;
	
	/** Platform name if authenticated. */
	protected String platformname;
	
	/** Networks shared with the sender. */
	protected Set<String> sharednetworks;
	
	/** Networks containing the sender. */
	protected Set<String> networks;
	
	/** Fixed roles of the sender. */
	protected volatile Set<String> fixedroles;
	
	/** Roles based on mapping of the sender. */
	protected volatile Set<String> mappedroles;
	
	/** Union of fixed and mapped roles. */
	protected volatile Set<String> roles;
	
	/**
	 *  Creates the infos.
	 */
	public SecurityInfo()
	{
		roles = Collections.emptySet();
	}
	
	/**
	 *  Checks if the sender platform has default authorization.
	 *
	 *  @return True if sender platform has default authorization.
	 */
//	public boolean hasDefaultAuthorization()
//	{
//		return allowdefaultauthorization && (trustedplatform || adminplatform || (sharednetworks != null && sharednetworks.size() > 0));
//	}
	
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
//	public boolean isAdminPlatform()
//	{
//		return adminplatform;
//	}

	/**
	 *  Sets the ID of the sender platform if it is trusted, null otherwise.
	 *
	 *  @param trustedplatform The ID of the sender platform if it is trusted, null otherwise.
	 */
//	public void setAdminPlatform(boolean adminplatform)
//	{
//		this.adminplatform = adminplatform;
//	}
	
	/**
	 *  Checks if the sender platform name is authenticated and trusted.
	 *
	 *  @return True, if trusted.
	 */
//	public boolean isTrustedPlatform()
//	{
//		return trustedplatform;
//	}

	/**
	 *  Sets if the sender platform name is authenticated and trusted.
	 *
	 *  @param trustedplatform True, if trusted.
	 */
//	public void setTrustedPlatform(boolean trustedplatform)
//	{
//		this.trustedplatform = trustedplatform;
//	}
	
	/**
	 *  Checks if default authorization is allowed.
	 *
	 *  @return True, if allowed.
	 */
//	public boolean isAllowDefaultAuthorization()
//	{
//		return allowdefaultauthorization;
//	}
	
	/**
	 *  Sets if default authorization is allowed.
	 *
	 *  @param allowdefaultauthorization True, if allowed.
	 */
//	public void setAllowDefaultAuthorization(boolean allowdefaultauthorization)
//	{
//		this.allowdefaultauthorization = allowdefaultauthorization;
//	}

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
	 *  Gets the authenticated networks of the sender.
	 *
	 *  @return The authenticated networks of the sender (sorted).
	 */
	public Set<String> getSharedNetworks()
	{
		return sharednetworks;
	}

	/**
	 *  Sets the networks.
	 *
	 *  @param networks The networks.
	 */
	public void setSharedNetworks(Set<String> sharednetworks)
	{
		this.sharednetworks = Collections.unmodifiableSet(sharednetworks);
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
	 *  Gets the fixed roles.
	 *
	 *  @return The fixed roles.
	 */
	public Set<String> getFixedRoles()
	{
		return fixedroles;
	}
	
	/**
	 *  Gets the mapped roles.
	 *
	 *  @return The mapped roles.
	 */
	public Set<String> getMappedRoles()
	{
		return mappedroles;
	}

	/**
	 *  Sets the fixed roles.
	 *
	 *  @param roles The fixed roles.
	 */
	public void setFixedRoles(Set<String> roles)
	{
		this.fixedroles = Collections.unmodifiableSet(roles);
		roles = new HashSet<>(fixedroles);
		if (mappedroles != null)
			roles.addAll(mappedroles);
		this.roles = Collections.unmodifiableSet(roles);
	}
	
	/**
	 *  Sets the mapped roles.
	 *
	 *  @param roles The mapped roles.
	 */
	public void setMappedRoles(Set<String> roles)
	{
		this.mappedroles = Collections.unmodifiableSet(roles);
		roles = new HashSet<>(mappedroles);
		if (fixedroles != null)
			roles.addAll(fixedroles);
		this.roles = Collections.unmodifiableSet(roles);
	}

	/**
	 *  Convert to string.
	 */
	public String toString()
	{
		return "Trusted: " + getRoles().contains(Security.TRUSTED) + ", Admin: " + getRoles().contains(Security.ADMIN) + ", Networks: " + Arrays.toString(networks.toArray()); 
	}
}
