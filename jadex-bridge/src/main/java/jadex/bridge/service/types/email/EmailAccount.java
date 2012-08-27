package jadex.bridge.service.types.email;

/**
 *  Email account data.
 */
public class EmailAccount
{
	/** Test account. */
	public static final EmailAccount TEST_ACCOUNT = new EmailAccount("jadexagent@gmail.com", "***REMOVED***", "jadexagent", 
		"***REMOVED***", 587, false, true,
		"imap.gmail.com", "imaps");
	
	//-------- attributes --------

	/** The user name. */
	protected String user;

	/** The user password. */
	protected String password;

	/** The sender email. */
	protected String sender;

	//-------- send info --------
	
	/** The smtp host address. */
	protected String smtphost;

	/** The smpt host port. */
	protected Integer smtpport;

	/** Flag if ssl should be used. */
	protected boolean ssl;

	/** Flag if starttls should be used. */
	protected boolean starttls;
	
	//-------- receive info --------
	
	/** The imap address. */
	protected String receivehost;
	
	/** The imaps flag. */
	protected String receiveprotocol;
	
	//-------- constructors --------

	/**
	 *  Create a new account.
	 *  @param smtphost The smtp host.
	 *  @param port The smtp port.
	 *  @param user The user name.
	 *  @param password The password.
	 *  @param sender The sender email.
	 *  @param ssl Flag for ssl.
	 */
	public EmailAccount(String user, String password, String sender, 
		String smtphost, Integer smtpport, boolean ssl, boolean starttls,
		String receivehost, String receiveprotocol)
	{
		this.smtphost = smtphost;
		this.smtpport = smtpport;
		this.user = user;
		this.password = password;
		this.sender = sender;
		this.ssl = ssl;
		this.starttls = starttls;
		this.receivehost = receivehost;
		this.receiveprotocol = receiveprotocol;
	}

	//-------- methods --------

	/**
	 *  Get host.
	 *  @return The host.
	 */
	public String getSmtpHost()
	{
		return smtphost;
	}

	/**
	 *  Set the host.
	 *  @param host The host.
	 */
	public void setSmtpHost(String host)
	{
		this.smtphost = host;
	}

	/**
	 *  Get the port.
	 *  @return The port.
	 */
	public Integer getSmtpPort()
	{
		return smtpport;
	}

	/**
	 *  Set the port.
	 *  @param port The port.
	 */
	public void setSmtpPort(Integer port)
	{
		this.smtpport = port;
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

	/**
	 *  Get the starttls.
	 *  @return The starttls.
	 */
	public boolean isStartTls()
	{
		return starttls;
	}

	/**
	 *  Set the starttls.
	 *  @param starttls The starttls to set.
	 */
	public void setStartTls(boolean starttls)
	{
		this.starttls = starttls;
	}

	/**
	 *  Get the receivehost.
	 *  @return The receivehost.
	 */
	public String getReceiveHost()
	{
		return receivehost;
	}

	/**
	 *  Set the receivehost.
	 *  @param receivehost The receivehost to set.
	 */
	public void setReceiveHost(String receivehost)
	{
		this.receivehost = receivehost;
	}

	/**
	 *  Get the receiveprotocol.
	 *  @return The receiveprotocol.
	 */
	public String getReceiveProtocol()
	{
		return receiveprotocol;
	}

	/**
	 *  Set the receiveprotocol.
	 *  @param receiveprotocol The receiveprotocol to set.
	 */
	public void setReceiveProtocol(String receiveprotocol)
	{
		this.receiveprotocol = receiveprotocol;
	}

	
}

