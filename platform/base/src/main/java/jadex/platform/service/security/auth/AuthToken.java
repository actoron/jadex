package jadex.platform.service.security.auth;

/**
 *  Basic authentication token.
 *
 */
public class AuthToken
{
	/** The main authentication data. */
	protected byte[] authdata;
	
	/**
	 *  Creates an empty token.
	 */
	public AuthToken()
	{
	}
	
	/**
	 *  Creates a token.
	 */
	public AuthToken(byte[] authdata)
	{
		this.authdata = authdata;
	}
	
	/**
	 *  Gets authentication data.
	 *  
	 *  @return Authentication data.
	 */
	public byte[] getAuthData()
	{
		return authdata;
	}
	
	/**
	 *  Sets authentication data.
	 *  
	 *  @param authdata Authentication data.
	 */
	public void setAuthData(byte[] authdata)
	{
		this.authdata = authdata;
	}
}
