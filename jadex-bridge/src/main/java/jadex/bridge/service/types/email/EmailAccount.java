package jadex.bridge.service.types.email;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import jadex.commons.SUtil;

/**
 *  Email account data.
 */
public class EmailAccount
{
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
	
	/** Flag if no authentication should be used. */
	protected boolean noauth;
	
	//-------- receive info --------
	
	/** The imap address. */
	protected String receivehost;
	
	/** The imaps flag. */
	protected String receiveprotocol;
	
	//-------- constructors --------

	/**
	 *  Create a new EmailAccount.
	 */
	public EmailAccount()
	{
	}
	
	/**
	 *  Create a new EmailAccount.
	 */
	public EmailAccount(String filename)
	{
		readAccount(filename);
	}
	
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
		String receivehost, String receiveprotocol, boolean noauth)
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
		this.noauth = noauth;
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
	
	/**
	 *  Get the noAuthentication.
	 *  @return The noAuthentication
	 */
	public boolean isNoAuthentication()
	{
		return noauth;
	}

	/**
	 *  Set the noAuthentication.
	 *  @param noAuthentication The noAuthentication to set
	 */
	public void setNoAuthentication(boolean noauth)
	{
		this.noauth = noauth;
	}

	/**
	 *  Store data to a property file.
	 */
	public void writeAccount(String filename)
	{
		try
		{
			Properties ps = new Properties();
			
			ps.setProperty("user", getUser());
			ps.setProperty("password", getPassword());
			ps.setProperty("sender", getSender());
			ps.setProperty("smtphost", getSmtpHost());
			ps.setProperty("smtpport", ""+getSmtpPort());
			ps.setProperty("ssl", ""+isSsl());
			ps.setProperty("starttls", ""+isStartTls());
			ps.setProperty("receivehost", getReceiveHost());
			ps.setProperty("receiveprotocol", getReceiveProtocol());
			ps.setProperty("noauth", ""+isNoAuthentication());
			
			OutputStream	os	= new FileOutputStream(new File(filename));
			ps.store(os, null);
			os.close();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Read account data from property file.
	 */
	public void readAccount(String filename)
	{
		try 
		{
			readAccount(new FileInputStream(filename));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Read account data from property file.
	 */
	public static EmailAccount createAccount(String filename, ClassLoader cl)
	{
		try 
		{
			EmailAccount ret = new EmailAccount();
			ret.readAccount(SUtil.getResource(filename, cl));
			return ret;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Read account data from property file.
	 */
	public void readAccount(InputStream	is)
	{
		try 
		{
			Properties ps = new Properties();
		    ps.load(is);
		    is.close();
		    
		    if(ps.getProperty("user")!=null)
		    	setUser(ps.getProperty("user"));
		    if(ps.getProperty("password")!=null)
		    	setPassword(ps.getProperty("password"));
		    if(ps.getProperty("sender")!=null)
		    	setSender(ps.getProperty("sender"));
		    if(ps.getProperty("smtphost")!=null)
		    	setSmtpHost(ps.getProperty("smtphost"));
		    if(ps.getProperty("smtpport")!=null)
		    	setSmtpPort(Integer.valueOf(ps.getProperty("smtpport")));
		    if(ps.getProperty("ssl")!=null)
		    	setSsl(Boolean.parseBoolean(ps.getProperty("ssl")));
		    if(ps.getProperty("starttls")!=null)
		    	setStartTls(Boolean.parseBoolean(ps.getProperty("starttls")));
		    if(ps.getProperty("noauth")!=null)
		    	setNoAuthentication(Boolean.parseBoolean(ps.getProperty("noauth")));
		    if(ps.getProperty("receivehost")!=null)
		    	setReceiveHost(ps.getProperty("receivehost"));
		    if(ps.getProperty("receiveprotocol")!=null)
		    	setReceiveProtocol(ps.getProperty("receiveprotocol"));
		} 
		catch(Exception e) 
		{
			throw new RuntimeException(e);
		}
	}
}

