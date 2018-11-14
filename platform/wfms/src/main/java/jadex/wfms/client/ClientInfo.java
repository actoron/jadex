package jadex.wfms.client;

public class ClientInfo
{
	/** The user name */
	protected String userName;
	
	/**
	 * Creates a new client info.
	 */
	public ClientInfo()
	{
	}
	
	/**
	 * Creates a new client info.
	 * @param userName user name.
	 */
	public ClientInfo(String userName)
	{
		this.userName = userName;
	}
	
	/**
	 * Returns the user name of the client.
	 * @return user name
	 */
	public String getUserName()
	{
		return userName;
	}
	
	/**
	 * Sets the user name of the client.
	 * @param username The user name.
	 */
	public void setUserName(String username)
	{
		this.userName = username;
	}
}
