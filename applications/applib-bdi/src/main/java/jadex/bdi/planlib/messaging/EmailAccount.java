package jadex.bdi.planlib.messaging;

/**
 *  Email account data.
 */
public class EmailAccount
{
	//-------- attributes --------

	/** The smtp host address. */
	protected String host;

	/** The smpt host port. */
	protected Integer port;

	/** The user name. */
	protected String user;

	/** The user password. */
	protected String password;

	/** The sender email. */
	protected String sender;

	/** Flag if ssl should be used. */
	protected boolean ssl;

	//-------- constructors --------

	/**
	 *  Create a new account.
	 *  @param host The smtp host.
	 *  @param port The smtp port.
	 *  @param user The user name.
	 *  @param password The password.
	 *  @param sender The sender email.
	 *  @param ssl Flag for ssl.
	 */
	public EmailAccount(String host, Integer port, String user, String password, String sender, boolean ssl)
	{
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.sender = sender;
		this.ssl = ssl;
	}

	//-------- methods --------

	/**
	 *  Get host.
	 *  @return The host.
	 */
	public String getHost()
	{
		return host;
	}

	/**
	 *  Set the host.
	 *  @param host The host.
	 */
	public void setHost(String host)
	{
		this.host = host;
	}

	/**
	 *  Get the port.
	 *  @return The port.
	 */
	public Integer getPort()
	{
		return port;
	}

	/**
	 *  Set the port.
	 *  @param port The port.
	 */
	public void setPort(Integer port)
	{
		this.port = port;
	}

	/**
	 *  Get the user.
	 *  @return The user.
	 */
	public String getUser()
	{
		return user;
	}

	/**
	 *  Set the user.
	 *  @param user The user.
	 */
	public void setUser(String user)
	{
		this.user = user;
	}

	/**
	 *  Get the password.
	 *  @return The password.
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 *  Set the password.
	 *  @param password The password.
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 *  Get the sender.
	 *  @return The sender.
	 */
	public String getSender()
	{
		return sender;
	}

	/**
	 *  Set the sender.
	 *  @param sender The sender.
	 */
	public void setSender(String sender)
	{
		this.sender = sender;
	}
	
	/**
	 *  Is ssl connection?
	 *  @return The
	 */
	public boolean isSsl()
	{
		return ssl;
	}

	/**
	 *  Set the ssl connection property.
	 *  @param ssl Flag indicating ssl.
	 */
	public void setSsl(boolean ssl)
	{
		this.ssl = ssl;
	}
}
