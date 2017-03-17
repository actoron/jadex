package jadex.bridge.service.types.email;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import jadex.commons.SUtil;
import jadex.commons.transformation.annotations.Exclude;

/**
 *  Email account data.
 *  Note, the file format has changed for Jadex 3.0,
 *  but still support reading the old format.
 */
public class EmailAccount
{
	//-------- attributes --------

	/** All settings are stored in standard javax.mail... and extended jadex.mail... properties. */
	protected Properties	props;
	
	//-------- constructors --------

	/**
	 *  Create a new EmailAccount.
	 */
	public EmailAccount()
	{
		this.props	= new Properties();
	}
	
	/**
	 *  Create a new EmailAccount.
	 */
	public EmailAccount(String filename)
	{
		readAccount(filename);
	}
	
	/**
	 *  Create an account with initial properties.
	 */
	public EmailAccount(Properties props)
	{
		setProperties(props);
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
		this.props	= new Properties();
		setUser(user);
		setPassword(password);
		setSender(sender);
		setSmtpHost(smtphost);
		setSmtpPort(smtpport);
		setSsl(ssl);
		setStartTls(starttls);
		setReceiveHost(receivehost);
		setReceiveProtocol(receiveprotocol);
		setNoAuthentication(noauth);
	}

	//-------- methods --------
	
	/**
	 *  Get the properties.
	 */
	public Properties	getProperties()
	{
		return (Properties)props.clone();
	}
	
	/**
	 *  Set the properties.
	 */
	public void	setProperties(Properties props)
	{
		this.props	= props!=null ? (Properties)props.clone() : new Properties();
	}

	/**
	 *  Get host.
	 *  @return The host.
	 */
	@Exclude
	public String getSmtpHost()
	{
		return props.getProperty("mail.smtp.host");
	}

	/**
	 *  Set the host.
	 *  @param host The host.
	 */
	@Exclude
	public void setSmtpHost(String host)
	{
		props.setProperty("mail.smtp.host", host);
	}

	/**
	 *  Get the port.
	 *  @return The port.
	 */
	@Exclude
	public Integer getSmtpPort()
	{
		return props.getProperty("mail.smtp.port")!=null ? Integer.valueOf(props.getProperty("mail.smtp.port")) : null;
	}

	/**
	 *  Set the port.
	 *  @param port The port.
	 */
	@Exclude
	public void setSmtpPort(Integer port)
	{
		props.setProperty("mail.smtp.port", port!=null ? ""+port : null);
	}

	/**
	 *  Get the user.
	 *  @return The user.
	 */
	@Exclude
	public String getUser()
	{
		return props.getProperty("jadex.mail.user");
	}

	/**
	 *  Set the user.
	 *  @param user The user.
	 */
	@Exclude
	public void setUser(String user)
	{
		props.setProperty("jadex.mail.user", user);
	}

	/**
	 *  Get the password.
	 *  @return The password.
	 */
	@Exclude
	public String getPassword()
	{
		return props.getProperty("jadex.mail.password");
	}

	/**
	 *  Set the password.
	 *  @param password The password.
	 */
	@Exclude
	public void setPassword(String password)
	{
		props.setProperty("jadex.mail.password", password);
	}

	/**
	 *  Get the sender.
	 *  @return The sender.
	 */
	@Exclude
	public String getSender()
	{
		return props.getProperty("mail.from");
	}

	/**
	 *  Set the sender.
	 *  @param sender The sender.
	 */
	@Exclude
	public void setSender(String sender)
	{
		props.setProperty("mail.from", sender);
	}
	
	/**
	 *  Is ssl connection?
	 *  @return The
	 */
	@Exclude
	public boolean isSsl()
	{
		return Boolean.valueOf(props.getProperty("mail.smtp.ssl.enable"));
	}

	/**
	 *  Set the ssl connection property.
	 *  @param ssl Flag indicating ssl.
	 */
	@Exclude
	public void setSsl(boolean ssl)
	{
		props.setProperty("mail.smtp.ssl.enable", Boolean.valueOf(ssl).toString());
	}

	/**
	 *  Get the starttls.
	 *  @return The starttls.
	 */
	@Exclude
	public boolean isStartTls()
	{
		return Boolean.valueOf(props.getProperty("mail.smtp.starttls.enable"));
	}

	/**
	 *  Set the starttls.
	 *  @param starttls The starttls to set.
	 */
	@Exclude
	public void setStartTls(boolean starttls)
	{
		props.setProperty("mail.smtp.starttls.enable", Boolean.valueOf(starttls).toString());
	}

	/**
	 *  Get the receivehost.
	 *  @return The receivehost.
	 */
	@Exclude
	public String getReceiveHost()
	{
		// No standard property :( only IMAP POP specific...
		return props.getProperty("jadex.mail.receivehost");
	}

	/**
	 *  Set the receivehost.
	 *  @param receivehost The receivehost to set.
	 */
	@Exclude
	public void setReceiveHost(String receivehost)
	{
		props.setProperty("jadex.mail.receivehost", receivehost);
	}

	/**
	 *  Get the receiveprotocol.
	 *  @return The receiveprotocol.
	 */
	@Exclude
	public String getReceiveProtocol()
	{
		return props.getProperty("mail.store.protocol");
	}

	/**
	 *  Set the receiveprotocol.
	 *  @param receiveprotocol The receiveprotocol to set.
	 */
	@Exclude
	public void setReceiveProtocol(String receiveprotocol)
	{
		props.setProperty("mail.store.protocol", receiveprotocol);
	}
	
	/**
	 *  Get the noAuthentication.
	 *  @return The noAuthentication
	 */
	@Exclude
	public boolean isNoAuthentication()
	{
		// Only no-auth when explicitly set to false (hack???)
		return Boolean.FALSE.toString().equals(props.getProperty("mail.smtp.auth"))
			|| Boolean.FALSE.toString().equals(props.getProperty("mail.smtps.auth"));
	}

	/**
	 *  Set the noAuthentication.
	 *  @param noAuthentication The noAuthentication to set
	 */
	@Exclude
	public void setNoAuthentication(boolean noauth)
	{
		// inverse condition (not no-auth...)
		props.setProperty("mail.smtp.auth", Boolean.valueOf(!noauth).toString());
		props.setProperty("mail.smtps.auth", Boolean.valueOf(!noauth).toString());
	}

	/**
	 *  Store data to a property file.
	 */
	public void writeAccount(String filename)
	{
		try
		{
			// Not actual VersionInfo value, only updated when format changes.
			props.setProperty("jadex.mail.version", "3.0.0");
			
			OutputStream	os	= new FileOutputStream(new File(filename));
			props.store(os, null);
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
			this.props = new Properties();
		    props.load(is);
		    is.close();
		    
		    // Convert old format to new.
		    if(props.getProperty("jadex.mail.version")==null)
		    {
				Properties ps	= props;
				this.props	= new Properties();
			    
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
		} 
		catch(Exception e) 
		{
			throw new RuntimeException(e);
		}
	}
}

