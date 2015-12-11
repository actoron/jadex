package jadex.bridge.service.types.security;

import java.util.List;

import jadex.bridge.service.annotation.Reference;


/**
 *  Interface for requests to be passed to the
 *  security service for validation.
 */
@Reference(remote=false)
public interface IAuthorizable
{
//	/**
//	 *  The origin of the request.
//	 *  May be used for blacklist/whitelist authentication.
//	 */
//	public IComponentIdentifier	getOrigin();
	
	/**
	 *  Get the security level of the request.
	 */
	public String	getSecurityLevel();
	
	/**
	 *  Get the digest content.
	 *  Could be part or the whole message depending on
	 *  security level that should be enforced.
	 */
	public String getDigestContent();
	
	/**
	 *  The time stamp of the request.
	 *  Used for digest authentication and preventing replay attacks.
	 *  Ignored, when no authentication is supplied.
	 */
	public long	getTimestamp();
	
	/** 
	 *  The validity duration.
	 */
	public long getValidityDuration();
	
	/**
	 *  Set the time stamp of the request.
	 *  Is automatically called by the security service if necessary, when the request is preprocessed.
	 *  @param timestamp	The time stamp.
	 */
	public void	setTimestamp(long timestamp);
	
	/**
	 *  The authentication data.
	 *  The data is calculated by building an MD5 hash from the target platform password, optionally message
	 *  content (=digest content) and the timestamp.
	 *  MD5 is fast, produces only small digests to reduce network traffic and can still be considered secure
	 *  for short periods of time.
	 */
	public List<byte[]>	getAuthenticationData();
	
	/**
	 *  Set the authentication data.
	 *  Is automatically called by the security service if necessary, when the request is preprocessed.
	 *  @param authdata	The authentication data.
	 */
	public void	setAuthenticationData(List<byte[]> authdata);
}
