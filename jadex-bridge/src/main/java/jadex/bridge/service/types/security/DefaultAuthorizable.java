package jadex.bridge.service.types.security;

public class DefaultAuthorizable implements IAuthorizable
{
	//-------- attributes --------
	
	/** The timestamp. */
	protected long	timestamp;
	
	/** The authentication data. */
	protected byte[]	authdata;
	
	//-------- constructors --------
	
	/**
	 *  Bean constructor.
	 */
	public DefaultAuthorizable()
	{
	}
	
	//-------- IAuthorizable interface --------
	
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
	public byte[]	getAuthenticationData()
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
	public void	setAuthenticationData(byte[] authdata)
	{
		this.authdata	= authdata;
	}
}
