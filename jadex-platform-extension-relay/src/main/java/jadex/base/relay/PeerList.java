package jadex.base.relay;

import jadex.platform.service.message.transport.httprelaymtp.RelayConnectionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

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
	
	/** Delay between two pings when a peer is connected. */
	public static final long	DELAY_ONLINE	= 30000;
	
	/** Delay between two pings when a peer is offline. */
	public static final long	DELAY_OFFLINE	= 30000;
	
	//-------- attributes --------
	
	/** The own url. */
	protected String	url;
	
	/** The known peers (url -> peer entry). */
	protected Map<String, PeerEntry>	peers;
	
	/** Timer for polling relay peers. */
	protected Timer	timer;
	
	/** The connection manager. */
	protected RelayConnectionManager	conman;

	//-------- constructors --------
	
	/**
	 *  Create a new peer list.
	 */
	public PeerList()
	{
		this.peers	= Collections.synchronizedMap(new HashMap<String, PeerEntry>());
		this.conman	= new RelayConnectionManager();
		
		Properties	props	= new Properties();
		File	propsfile	= new File(RelayHandler.SYSTEMDIR, "peer.properties");
		if(propsfile.exists())
		{
			try
			{
				InputStream	fis	= new FileInputStream(propsfile);
				props.load(fis);
				fis.close();
			}
			catch(Exception e)
			{
				RelayHandler.getLogger().warning("Relay failed to load: "+propsfile);
			}
		}
		else
		{
			try
			{
				props.setProperty(PROPERTY_URL, "");
				props.setProperty(PROPERTY_PEERS, "");
				OutputStream	fos	= new FileOutputStream(propsfile);
				props.store(fos, " Relay peer properties.\n"
					+" Specify settings below to enable load balancing and exchanging awareness information with other relay servers.\n"
					+" Use '"+PROPERTY_URL+"' for this relay's own public URL.\n"
					+" Use '"+PROPERTY_PEERS+"' for a comma separated list of peer server urls.");
				fos.close();
			}
			catch(Exception e)
			{
				RelayHandler.getLogger().warning("Relay failed to save: "+propsfile);
			}
		}
		
		// Todo: check that specified url is valid and connects to this server.
		this.url	= props.containsKey(PROPERTY_URL) && !"".equals(props.getProperty(PROPERTY_URL))
			? RelayConnectionManager.relayAddress(props.getProperty(PROPERTY_URL)) : "";
		
		if(props.containsKey(PROPERTY_PEERS))
		{
			StringTokenizer	stok	= new StringTokenizer(props.getProperty(PROPERTY_PEERS), ",");
			while(stok.hasMoreTokens())
			{
				addPeer(stok.nextToken().trim(), true);
			}
		}
	}
	
	/**
	 *  Stop any activities for managing the peer list.
	 */
	public void	dispose()
	{
		if(timer!=null)
		{
			timer.cancel();
		}
		conman.dispose();
	}
	
	//-------- methods --------
	
	/**
	 *  Get the public url of this relay, if known.
	 */
	public String	getUrl()
	{
		return url;
	}
	
	/**
	 *  Get the known relay urls.
	 *  If no urls are known, the request url is returned.
	 */
	public String	getURLs(String request)
	{
		String	ret;
		
		// Fallback, when no peers specified.
		if("".equals(url))
		{
			ret	= request;
			if(ret.endsWith("/servers"))
			{
				ret	= RelayConnectionManager.relayAddress(ret.substring(0, ret.length()-7));
			}
		}
		
		// Build list of currently connected peers.
		else
		{
			ret	= url;
			PeerEntry[] apeers = getPeers();
			for(PeerEntry peer: apeers)
			{
				if(peer.isConnected())
				{
					ret	+= ", "+peer.getUrl();
				}
			}
		}
		return ret;
	}

	/**
	 *  Get the currently connected peers.
	 */
	public PeerEntry[] getPeers()
	{
		// Fetch array to avoid concurrency problems
		return peers.values().toArray(new PeerEntry[0]);
	}
	
	/**
	 *  Add a peer that requested a connection.
	 */
	public PeerEntry	addPeer(String peerurl, boolean initial)
	{
		PeerEntry	peer;
		if("".equals(url))
		{
			throw new RuntimeException("No peer connections allowed, if local URL not set.");
		}
		else
		{
			peerurl	= RelayConnectionManager.relayAddress(peerurl);
			peer	= peers.get(peerurl);
			if(!url.equals(peerurl) && peer==null)
			{
				peer	= new PeerEntry(peerurl, initial);
				peers.put(peerurl, peer);
				RelayHandler.getLogger().info("Peer added: "+peer.getUrl());
	
				// Create timer on demand.
				if(timer==null)
				{
					synchronized(this)
					{
						if(timer==null)
						{
							this.timer	= new Timer(true);
						}
					}
				}
				
				// Periodically test connection to peer.
				timer.schedule(new PeerTimerTask(peer), 0);		
			}
		}
		return peer;
	}

	//-------- helper classes --------
	
	/**
	 *  Timer task to periodically ping remote peers.
	 */
	public class PeerTimerTask extends TimerTask
	{
		//-------- attributes --------
		
		/** The peer. */
		protected PeerEntry	peer;
		
		//-------- constructors --------
		
		/**
		 *  Create a timer task.
		 */
		public PeerTimerTask(PeerEntry peer)
		{
			this.peer = peer;
		}
		
		//-------- methods --------

		/**
		 *  Execute the timer task.
		 */
		public void run()
		{
			try
			{
				// Try to connect and add new peers, if any.
				String	servers	= conman.getPeerServers(peer.getUrl(), url);
				peer.setConnected(true);
				for(StringTokenizer stok=new StringTokenizer(servers, ","); stok.hasMoreTokens(); )
				{
					addPeer(stok.nextToken().trim(), false);
				}
			}
			catch(IOException e)
			{
				peer.setConnected(false);
			}
			
			if(peer.isConnected())
			{
				timer.schedule(new PeerTimerTask(peer), DELAY_ONLINE);
			}
			else if(peer.isInitial())
			{
				timer.schedule(new PeerTimerTask(peer), DELAY_OFFLINE);					
			}
			else
			{
				peers.remove(peer.getUrl());
				RelayHandler.getLogger().info("Peer removed: "+peer.getUrl());
			}
		}
	}
}
