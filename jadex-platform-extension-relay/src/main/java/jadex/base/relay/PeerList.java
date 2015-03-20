package jadex.base.relay;

import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
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
import java.util.UUID;

/**
 *  The peer list actively manages the list of
 *  connected peer relay servers.
 */
public class PeerList
{
	//-------- constants --------
	
	/** The event for an added peer. */
	public static final String	EVENT_ADDED	= "added";

	/** The event for a removed peer. */
	public static final String	EVENT_REMOVED	= "removed";

	/** The event when a peer becomes online. */
	public static final String	EVENT_ONLINE	= "online";
	
	/** The event when a peer becomes offline. */
	public static final String	EVENT_OFFLINE	= "offline";

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
	
	/** The peer handlers for polling relay peers (url -> peer handler). */
	protected Map<String, PeerHandler>	handlers;
	
	/** The connection manager. */
	protected RelayConnectionManager	conman;
	
	/** The connection history db (if any). */
	protected StatsDB	db;
	
	/** Change listeners. */
	protected List<IChangeListener<PeerEntry>>	listeners;

	/**	Flag to enable debug text being generated (set debug=true or 0..3 in peer.properties). */
	protected int	debug;

	//-------- constructors --------
	
	/**
	 *  Create a new peer list.
	 */
	public PeerList()
	{
		this.peers	= Collections.synchronizedMap(new HashMap<String, PeerEntry>());
		this.handlers	= Collections.synchronizedMap(new HashMap<String, PeerHandler>());
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
						+" Set '"+PROPERTY_DEBUG+"=true' or '"+PROPERTY_DEBUG+"=0..3' for enabling debugging output in html tooltips of peer relay table (optional, 0 means off, 3 is fine grained debug about single platforms).");
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
				props.setProperty(PROPERTY_DEBUG, "0");
				OutputStream	fos	= new FileOutputStream(propsfile);
				props.store(fos, " Relay peer properties.\n"
					+" Specify settings below to enable load balancing and exchanging awareness information with other relay servers.\n"
					+" '"+PROPERTY_ID+"' is this relay's own generated ID to differentiate entries from different peers in shared history information.\n"
					+" Set '"+PROPERTY_URL+"' to this relay's own publically accessible URL, e.g., http://www.mydomain.com:8080/relay (required for enabling peer-to-peer behavior).\n"
					+" Set '"+PROPERTY_PEERS+"' to a comma separated list of peer server urls to connect to at startup (optional, if this relay should only respond to connections from other peers).\n"
					+" Set '"+PROPERTY_DEBUG+"=true' or '"+PROPERTY_DEBUG+"=0..3' for enabling debugging output in html tooltips of peer relay table (optional, 0 means off, 3 is fine grained debug about single platforms).");
				fos.close();
			}
			catch(Exception e)
			{
				RelayHandler.getLogger().warning("Relay failed to save: "+propsfile);
			}
		}
		
		try
		{
			this.debug	= "true".equals(props.getProperty(PROPERTY_DEBUG)) ? 3 : Integer.parseInt(props.getProperty(PROPERTY_DEBUG));
		}
		catch(Exception e)
		{
			this.debug	= 0;
		}
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
		for(PeerHandler handler: handlers.values().toArray(new PeerHandler[0]))
		{
			handler.shutdown();
		}
		conman.dispose();
	}
	
	//-------- methods --------
	
	/**
	 *  Set the stats db to allow history replication between peers.
	 */
	public void	setDB(StatsDB db)
	{
		this.db	= db;
	}
	
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
	 *  Add a peer found in a servers list.
	 *  @param peerurl	The remote peer url.
	 *  @param initial	Denotes an initial peer as specified in the properties of this relay. Initial peers are not removed when they are offline.
	 */
	public PeerEntry	addPeer(String peerurl, boolean initial)
	{
		return addPeer(peerurl, null, -1, initial);
	}
	
	/**
	 *  Add a that requested a connection.
	 *  Also called for the continuous pings sent by connected peers.
	 *  @param peerurl	The remote peer url.
	 *  @param peerid	Contains the id of the remote peer.
	 *  @param peerstate	Contains id of the latest history entry of that peer to enable synchronization.
	 */
	public PeerEntry	addPeer(String peerurl, String peerid, int peerstate)
	{
		return addPeer(peerurl, peerid, peerstate, false);
	}
	
	/**
	 *  Add a peer found in a servers list or a peer that requested a connection.
	 *  Also called for the continuous pings sent by connected peers.
	 *  @param peerurl	The remote peer url.
	 *  @param peerid	If called from remote peer, contains the id of that peer.
	 *  @param peerstate	If called from remote peer, contains id of the latest history entry of that peer to enable synchronization.
	 *  @param initial	Denotes an initial peer as specified in the properties of this relay. Initial peers are not removed when they are offline.
	 */
	protected PeerEntry	addPeer(String peerurl, String peerid, int peerstate, boolean initial)
	{
		PeerEntry	peer	= null;
		if("".equals(url))
		{
			throw new RuntimeException("No peer connections allowed, if local URL not set.");
		}
		else
		{
			peerurl	= RelayConnectionManager.relayAddress(peerurl);
			if(!url.equals(peerurl))
			{
				boolean	added	= false;
				synchronized(this)
				{
					peer	= peers.get(peerurl);
					if(peer==null)
					{
						added	= true;
						peer	= new PeerEntry(peerurl, initial, debug);
						peers.put(peerurl, peer);
					}
				}
				
				if(added)
				{
					informListeners(new ChangeEvent<PeerEntry>(PeerList.this, EVENT_ADDED, peer));
					PeerHandler	handler	= new PeerHandler(peer);
					handlers.put(peerurl, handler);
					new Thread(handler).start();
				}
				
				// DB synchronization.
				if(peerid!=null && peerstate!=-1)
				{
					PeerHandler	handler	= handlers.get(peerurl);
					if(handler!=null)
					{
						handler.setPeerState(peerid, peerstate);
					}
				}
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
	 *  Handler to periodically ping remote peer and synchronize history db.
	 */
	public class PeerHandler implements Runnable
	{
		//-------- attributes --------
		
		/** The peer. */
		protected PeerEntry	peer;
		
		/** The peer id (null, if unknown). */
		protected String	peerid;
		
		/** The latest known remote peer state (-1 for none). */
		protected int	peerstate;
		
		/** The shutdown flag. */
		protected boolean	shutdown;
		
		//-------- constructors --------
		
		/**
		 *  Create a timer task.
		 */
		public PeerHandler(PeerEntry peer)
		{
			this.peer = peer;
			this.peerid	= null;
			this.peerstate	= -1;
		}
		
		//-------- methods --------

		/**
		 *  Execute the handler.
		 */
		public void run()
		{
			while(!shutdown)
			{
				// Perform DB synchronization, if required.
				if(peerstate!=-1 && db!=null)
				{
					int	localstate	= db.getLatestEntry(peerid);
					if(localstate<peerstate)
					{
						peer.addDebugText(2, "DB synchronization with: "+peer.getUrl()+", local="+localstate+", remote="+peerstate);
						RelayHandler.getLogger().info("DB synchronization with: "+peer.getUrl()+", local="+localstate+", remote="+peerstate);
						// Todo: fetch update from remote peer
						// conman.getDBUpdate(peer.getUrl(), localstate);
						peerstate	= -1;
					}
				}
				
				// Else ping remote peer
				else
				{
					boolean	connected	= peer.isConnected();
					try
					{
						// Try to connect and add new peers, if any.
	//					peer.addDebugText("Pinging peer");
						int	dbstate	= db!=null ? db.getLatestEntry(id) : -1;
						String	servers	= conman.getPeerServers(peer.getUrl(), url, id, dbstate, !peer.isConnected());
						peer.setConnected(true);
						for(StringTokenizer stok=new StringTokenizer(servers, ","); stok.hasMoreTokens(); )
						{
							addPeer(stok.nextToken().trim(), false);
						}
					}
					catch(IOException e)
					{
						peer.addDebugText(2, "Exception pinging peer: "+e);
						peer.setConnected(false);
					}
					
					if(peer.isConnected())
					{
						if(connected!=peer.isConnected())
						{
							informListeners(new ChangeEvent<PeerEntry>(PeerList.this, EVENT_ONLINE, peer));
						}
						
						synchronized(this)
						{
							try
							{
								this.wait(DELAY_ONLINE);
							}
							catch(InterruptedException e)
							{
							}
						}
					}
					else if(peer.isInitial())
					{
						if(connected!=peer.isConnected())
						{
							informListeners(new ChangeEvent<PeerEntry>(PeerList.this, EVENT_OFFLINE, peer));
						}
	
						synchronized(this)
						{
							try
							{
								this.wait(DELAY_OFFLINE);
							}
							catch(InterruptedException e)
							{
							}
						}
					}
					else
					{
						shutdown	= true;
						peers.remove(peer.getUrl());
						handlers.remove(peer.getUrl());
						informListeners(new ChangeEvent<PeerEntry>(PeerList.this, EVENT_REMOVED, peer));
					}
				}
			}
		}
		
		/**
		 *  Set the peer state received from remote.
		 *  Starts db synchronization, if necessary.
		 */
		public void	setPeerState(String peerid, int peerstate)
		{
			this.peerid	= peerid;
			this.peerstate	= peerstate;
			synchronized(this)
			{
				this.notify();
			}
		}
		
		/**
		 *  Shutdown the handler.
		 */
		public void	shutdown()
		{
			shutdown	= true;
			synchronized(this)
			{
				this.notify();
			}
		}
	}
}
