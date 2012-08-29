package jadex.bridge.service.types.security;

import jadex.bridge.service.annotation.Security;

import java.util.List;

/**
 *  Default implementation for a authorizable.
 */
public class DefaultAuthorizable implements IAuthorizable
{
	//-------- attributes --------
	
	/** The timestamp. */
	protected long	timestamp;
	
	/** The authentication data. */
	protected List<byte[]>	authdata;
	
	/** The digest content (the content the digest should be applied to). */
	protected String dcontent;
	
	//-------- constructors --------
	
	/**
	 *  Bean constructor.
	 */
	public DefaultAuthorizable()
	{
		// per default no digest content (for messages)
		dcontent = "";
	}
	
	//-------- IAuthorizable interface --------
	
	/**
	 *  Get the security level of the request.
	 */
	public String	getSecurityLevel()
	{
		// As default use maximum security.
		return Security.PASSWORD;
	}
	
	/**
	 *  Get the digestContent.
	 *  @return The digestContent.
	 */
	public String getDigestContent()
	{
		return dcontent;
	}

	/**
	 *  Set the digestContent.
	 *  @param digestContent The digestContent to set.
	 */
	public void setDigestContent(String digestContent)
	{
		this.dcontent = digestContent;
	}

	/**
	 *  The time stamp of the command.
	 *  Used for digest authentication and preventing replay attacks.
	 *  Ignored, when no authentication is supplied.
	 */
	public long	getTimestamp()
	{
		return timestamp;
	}
	
	/**
	 *  The authentication data.
	 *  The data is calculated by building an MD5 hash from the target platform password and the timestamp.
	 */
	public List<byte[]>	getAuthenticationData()
	{
		return authdata;
	}
	
	/**
	 *  Set the time stamp of the command.
	 */
	public void	setTimestamp(long timestamp)
	{
		this.timestamp	= timestamp;
	}
	
	/**
	 *  Set the authentication data.
	 */
	public void	setAuthenticationData(List<byte[]> authdata)
	{
		this.authdata	= authdata;
	}
}
