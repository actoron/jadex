package jadex.base.relay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.UUID;

/**
 *  The peer list actively manages the list of
 *  connected peer relay servers.
 */
public class PeerList
{
	//-------- constants --------
	
	/** The property for this relay's own url. */
	public static final String	PROPERTY_URL	= "url";
	
	/** The property for the peer server urls (comma separated). */
	public static final String	PROPERTY_PEERS	= "initial_peers";
	
	//-------- attributes --------
	
	/** The own id. */
	protected String	id;
	
	//-------- constructors --------
	
	/**
	 *  Create a new peer list.
	 */
	public PeerList()
	{
		id	= UUID.randomUUID().toString();
		
		Properties	props	= new Properties();
		File	propsfile	= new File(RelayServlet.SYSTEMDIR, "peer.properties");
		if(propsfile.exists())
		{
			try
			{
				props.load(new FileInputStream(propsfile));
			}
			catch(Exception e)
			{
				System.out.println("Relay failed to load: "+propsfile);
			}
		}
		else
		{
			try
			{
				props.setProperty(PROPERTY_URL, "");
				props.setProperty(PROPERTY_PEERS, "");
				props.store(new FileOutputStream(propsfile), " Relay peer properties.\n"
					+" Specify settings below to enable load balancing and exchanging awareness information with other relay servers.\n"
					+" Use '"+PROPERTY_URL+"' for this relay's own public URL.\n"
					+" Use '"+PROPERTY_PEERS+"' for a comma separated list of peer server urls.");
			}
			catch(Exception e)
			{
				System.out.println("Relay failed to save: "+propsfile);
			}
		}
	}
}
