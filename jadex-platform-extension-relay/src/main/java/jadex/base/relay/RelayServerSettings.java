package jadex.base.relay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.UUID;

/**
 *  Class to load/save relay settings.
 */
public class RelayServerSettings
{
	//-------- constants --------
	
	/** The property for this relay's own id. */
	public static final String	PROPERTY_ID	= "id";
	
	/** The property for this relay's own url. */
	public static final String	PROPERTY_URL	= "url";
	
	/** The property for the peer server urls (comma separated). */
	public static final String	PROPERTY_PEERS	= "initial_peers";
	
	/** The property for the debug flag. */
	public static final String	PROPERTY_DEBUG	= "debug";
	
	/** The property for the db synchronization flag. */
	public static final String	PROPERTY_DBSYNC	= "dbsync";

	/** The property for the flag for disabling platform connections. */
	public static final String	PROPERTY_NOCONNECTIONS	= "no_connections";

	//-------- attributes --------
	
	/** The properties holding the settings. */
	protected Properties	props;
	
	//-------- constructors --------
	
	/**
	 *  Create the server settings object.
	 */
	public RelayServerSettings()
	{
		this.props	= new Properties();
	}
	
	//-------- methods --------
	
	/**
	 *  Get the id.
	 */
	public String	getId()
	{
		return props.getProperty(PROPERTY_ID);
	}
	
	/**
	 *  Test if a public url is given.
	 */
	public boolean isUrlSpecified()
	{
		return props.containsKey(PROPERTY_URL) && !"".equals(props.getProperty(PROPERTY_URL));
	}
	
	/**
	 *  Get the url.
	 */
	public String	getUrl()
	{
		return props.getProperty(PROPERTY_URL);
	}

	/**
	 * Set the url.
	 */
	public void setUrl(String url)
	{
		props.setProperty(PROPERTY_URL, url);
	}
	
	/**
	 *  Get the initial peers.
	 */
	public String	getInitialPeers()
	{
		return props.getProperty(PROPERTY_PEERS);
	}
	
	/**
	 *  Get the debug level.
	 */
	public int	getDebugLevel()
	{
		int	debug;
		try
		{
			debug	= "true".equals(props.getProperty(PROPERTY_DEBUG)) ? 3 : Integer.parseInt(props.getProperty(PROPERTY_DEBUG));
		}
		catch(Exception e)
		{
			debug	= 0;
		}
		return debug;
	}
	
	/**
	 *  Get the debug level.
	 */
	public boolean	isDBSync()
	{
		return "true".equals(props.getProperty(PROPERTY_DBSYNC));
	}
	
	/**
	 *  Get the no connections flag.
	 */
	public boolean	isNoConnections()
	{
		return "true".equals(props.getProperty(PROPERTY_NOCONNECTIONS));
	}
	
	/**
	 *  Load settings.
	 *  @param file	The file name to load.
	 *  @param create	Flag to indicate that the file should be created, if it does not exist.
	 */
	public void	loadSettings(File file, boolean create)	throws Exception
	{
		if(file.exists())
		{
			InputStream	fis	= new FileInputStream(file);
			props.load(fis);
			fis.close();
		}
		
		// If id not set, create one on first access.
		if(getId()==null && create)
		{
			props.setProperty(PROPERTY_ID, UUID.randomUUID().toString());
			save(file);
		}
	}
	
	/**
	 *  Save the settings.
	 */
	public void	save(File file)	throws Exception
	{
		OutputStream	fos	= new FileOutputStream(file);
		props.store(fos, " Relay peer properties.\n"
			+" Specify settings below to enable load balancing and exchanging awareness information with other relay servers.\n"
			+" '"+PROPERTY_ID+"' is this relay's own generated ID to differentiate entries from different peers in shared history information.\n"
			+" Set '"+PROPERTY_URL+"' to this relay's own publically accessible URL, e.g., http://www.mydomain.com:8080/relay (required for enabling peer-to-peer behavior).\n"
			+" Set '"+PROPERTY_PEERS+"' to a comma separated list of peer server urls to connect to at startup (optional, if this relay should only respond to connections from other peers).\n"
			+" Set '"+PROPERTY_DBSYNC+"' to true, if synchronization with other relay history DBs is desired (optional).\n"
			+" Set '"+PROPERTY_NOCONNECTIONS+"' to true, if you want to prevent platforms from connecting to this relay, e.g. use this relay only to find other peers or for db sync of old history entries (optional).\n"
			+" Set '"+PROPERTY_DEBUG+"=true' or '"+PROPERTY_DEBUG+"=0..3' for enabling debugging output in html tooltips of peer relay table (optional, 0 means off, 3 is fine grained debug about single platforms).");
		fos.close();
	}
}
