package jadex.platform.service.security;

import java.util.Arrays;

import jadex.bridge.service.types.security.IMsgSecurityInfos;

/**
 *  Security meta-information of a message;
 *
 */
public class MsgSecurityInfos implements IMsgSecurityInfos
{
	/** Flag if the platform ID could be authenticated. */
	protected boolean authplatform;
	
	/** Flag if the platform is trusted. */
	protected boolean trustedplatform;
	
	/** Networks containing the sender. */
	protected String[] networks;
	
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
	public boolean isAuthenticatedPlatform()
	{
		return authplatform;
	}



	/**
	 *  Sets if the sender platform ID is authenticated.
	 *
	 *  @param authplatform True if authenticated.
	 */
	public void setAuthenticatedPlatform(boolean authplatform)
	{
		this.authplatform = authplatform;
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
	 *  Convert to string.
	 */
	public String toString()
	{
		return "Authenticated: " + authplatform + ", Trusted: " + trustedplatform + ", Networks: " + Arrays.toString(networks); 
	}
}
