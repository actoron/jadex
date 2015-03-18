package jadex.base.relay;

import jadex.commons.Base64;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.SUtil;
import jadex.platform.service.message.transport.httprelaymtp.RelayConnectionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 *  The peer list actively manages the list of
 *  connected peer relay servers.
 */
public class PeerList
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
	
	/** Delay between two pings when a peer is connected. */
	public static final long	DELAY_ONLINE	= 30000;
	
	/** Delay between two pings when a peer is offline. */
	public static final long	DELAY_OFFLINE	= 30000;
	
	//-------- attributes --------
	
	/** The own peer id (generated on first use). */
	protected String	id;
	
	/** The own url. */
	protected String	url;
	
	/** The known peers (url -> peer entry). */
	protected Map<String, PeerEntry>	peers;
	
	/** Timer for polling relay peers. */
	protected volatile Timer	timer;
	
	/** The connection manager. */
	protected RelayConnectionManager	conman;
	
	/** Change listeners. */
	protected List<IChangeListener<PeerEntry>>	listeners;

	/**	Flag to enable debug text being generated (set debug=true in peer.properties). */
	protected boolean	debug;

	//-------- constructors --------
	
	/**
	 *  Create a new peer list.
	 */
	public PeerList()
	{
		this.peers	= Collections.synchronizedMap(new HashMap<String, PeerEntry>());
		this.listeners	= Collections.synchronizedList(new ArrayList<IChangeListener<PeerEntry>>());
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
				
				if(props.getProperty(PROPERTY_ID)==null || props.getProperty(PROPERTY_ID).equals(""))
				{
					props.setProperty(PROPERTY_ID, UUID.randomUUID().toString());
					OutputStream	fos	= new FileOutputStream(propsfile);
					props.store(fos, " Relay peer properties.\n"
						+" Specify settings below to enable load balancing and exchanging awareness information with other relay servers.\n"
						+" '"+PROPERTY_ID+"' is this relay's own generated ID to differentiate entries from different peers in shared history information.\n"
						+" Set '"+PROPERTY_URL+"' to this relay's own publically accessible URL, e.g., http://www.mydomain.com:8080/relay (required for enabling peer-to-peer behavior).\n"
						+" Set '"+PROPERTY_PEERS+"' to a comma separated list of peer server urls to connect to at startup (optional, if this relay should only respond to connections from other peers).\n"
						+" Set '"+PROPERTY_DEBUG+"=true' for enabling debugging output in html tooltips of peer relay table (optional).");
					fos.close();
				}
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
				props.setProperty(PROPERTY_ID, UUID.randomUUID().toString());
				props.setProperty(PROPERTY_URL, "");
				props.setProperty(PROPERTY_PEERS, "");
				props.setProperty(PROPERTY_DEBUG, "false");
				OutputStream	fos	= new FileOutputStream(propsfile);
				props.store(fos, " Relay peer properties.\n"
					+" Specify settings below to enable load balancing and exchanging awareness information with other relay servers.\n"
					+" '"+PROPERTY_ID+"' is this relay's own generated ID to differentiate entries from different peers in shared history information.\n"
					+" Set '"+PROPERTY_URL+"' to this relay's own publically accessible URL, e.g., http://www.mydomain.com:8080/relay (required for enabling peer-to-peer behavior).\n"
					+" Set '"+PROPERTY_PEERS+"' to a comma separated list of peer server urls to connect to at startup (optional, if this relay should only respond to connections from other peers).\n"
					+" Set '"+PROPERTY_DEBUG+"=true' for enabling debugging output in html tooltips of peer relay table (optional).");
				fos.close();
			}
			catch(Exception e)
			{
				RelayHandler.getLogger().warning("Relay failed to save: "+propsfile);
			}
		}
		
		this.debug	= "true".equals(props.getProperty(PROPERTY_DEBUG));
		this.id	= props.getProperty(PROPERTY_ID);
		
		// Todo: check that specified url is valid and connects to this server.
		this.url	= props.containsKey(PROPERTY_URL) && !"".equals(props.getProperty(PROPERTY_URL))
			? RelayConnectionManager.relayAddress(props.getProperty(PROPERTY_URL)) : "";
			
		RelayHandler.getLogger().info("Relay url: "+url);
		
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
	 *  Get the peer id of this relay.
	 */
	public String	getId()
	{
		return id;
	}
	
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
				ret	= ret.substring(0, ret.length()-7);
			}
			ret	=  RelayConnectionManager.relayAddress(ret);
		}
		
		// Build list of currently connected peers.
		else
		{
			StringBuffer	sret	= new StringBuffer();
			sret.append(url);
			PeerEntry[] apeers = getPeers();
			for(PeerEntry peer: apeers)
			{
				if(peer.isConnected())
				{
					sret.append(", ");
					sret.append(peer.getUrl());						
				}
			}
			ret	= sret.toString();
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
	 *  Test if the given platform is connected to some peer.
	 */
	public boolean	checkPlatform(String id)
	{
		boolean	found	= false;
		for(PeerEntry peer: peers.values().toArray(new PeerEntry[0]))
		{
			if(peer.checkPlatform(id))
			{
				found	= true;
				break;
			}
		}
		return found;
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
				peer	= new PeerEntry(peerurl, initial, debug);
				peers.put(peerurl, peer);
				informListeners(new ChangeEvent<PeerEntry>(PeerList.this, "added", peer));
	
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
	
	/**
	 *  Add a change listener.
	 */
	public void	addChangeListener(IChangeListener<PeerEntry> lis)
	{
		listeners.add(lis);
	}

	/**
	 *  Remove a change listener.
	 */
	public void	removeChangeListener(IChangeListener<PeerEntry> lis)
	{
		listeners.remove(lis);
	}
	
	/**
	 *  inform listeners about an event.
	 */
	protected void	informListeners(ChangeEvent<PeerEntry> event)
	{
		IChangeListener<PeerEntry>[]	alis	= listeners.toArray(new IChangeListener[0]);
		for(IChangeListener<PeerEntry> lis: alis)
		{
			lis.changeOccurred(event);
		}
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
			boolean	connected	= peer.isConnected();
			try
			{
				// Try to connect and add new peers, if any.
//				peer.addDebugText("Pinging peer");
				String	servers	= conman.getPeerServers(peer.getUrl(), url, !peer.isConnected());
				peer.setConnected(true);
				for(StringTokenizer stok=new StringTokenizer(servers, ","); stok.hasMoreTokens(); )
				{
					addPeer(stok.nextToken().trim(), false);
				}
			}
			catch(IOException e)
			{
				peer.addDebugText("Exception pinging peer: "+e);
				peer.setConnected(false);
			}
			
			if(peer.isConnected())
			{
				timer.schedule(new PeerTimerTask(peer), DELAY_ONLINE);
				if(connected!=peer.isConnected())
				{
					informListeners(new ChangeEvent<PeerEntry>(PeerList.this, "online", peer));
				}
			}
			else if(peer.isInitial())
			{
				timer.schedule(new PeerTimerTask(peer), DELAY_OFFLINE);
				if(connected!=peer.isConnected())
				{
					informListeners(new ChangeEvent<PeerEntry>(PeerList.this, "offline", peer));
				}
			}
			else
			{
				peers.remove(peer.getUrl());
				informListeners(new ChangeEvent<PeerEntry>(PeerList.this, "removed", peer));
			}
		}
	}
}
