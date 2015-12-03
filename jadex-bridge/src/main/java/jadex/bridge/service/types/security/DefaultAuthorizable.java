package jadex.bridge.service.types.security;

import java.util.List;

import jadex.bridge.service.annotation.Security;

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
	
	/** The validity duration. */
	protected long dur;
	
	//-------- constructors --------
	
	/**
	 *  Bean constructor.
	 */
	public DefaultAuthorizable()
	{
		dcontent = ""; // per default no digest content (for messages)
//		dur = 65536; // one minute per default
		dur = 0; // if used this way cannot communicate with old platforms
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
	 *  Get the validity duration (in millis).
	 *  @return The validity duration (in millis).
	 */
	public long getValidityDuration()
	{
		return dur;
	}

	/**
	 *  Set the validity duration (in millis).
	 *  Will be set to its log2 value. 
	 *  @param dur The validity duration to set (in millis).
	 */
	public void setValidityDuration(long dur)
	{
		this.dur = dur;
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
	 *  @param dcontent The digestContent to set.
	 */
	public void setDigestContent(String dcontent)
	{
		this.dcontent = dcontent;
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
