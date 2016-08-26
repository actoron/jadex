package jadex.base.relay;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.commons.transformation.binaryserializer.SBinarySerializer;
import jadex.platform.service.message.MapSendTask;
import jadex.platform.service.message.RemoteMarshalingConfig;
import jadex.platform.service.message.transport.httprelaymtp.RelayConnectionManager;

/**
 *  Handler to periodically ping remote peer and synchronize history db.
 */
public class PeerHandler implements Runnable
{
	//-------- constants --------
	
	/** Delay between two pings when a peer is connected. */
	public static final long	DELAY_ONLINE	= 30000;
	
	/** Delay between two pings when a peer is offline. */
	public static final long	DELAY_OFFLINE	= 30000;
	
	//-------- attributes --------

	/** The relay handler. */
	protected RelayHandler	handler;
	
	/** The URL of the peer. */
	protected String	url;
	
	/** Is the peer one of the initial peers? */
	protected boolean	initial;
	
	/** Is this peer connected? */
	protected boolean	connected;
	
	/** The current platform infos received from the peer (platform id->info). */
	protected Map<String, PlatformInfo>	infos;
	
	/** Have initial platform infos been sent already? */
	protected boolean	sent;
	
	/** Debug information as multi-line xml text. */
	protected String	debugtext;
	
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
	public PeerHandler(RelayHandler handler, String url, boolean initial)
	{
		this.handler	= handler;
		this.url	= url;
		this.initial	= initial;
		this.infos	= Collections.synchronizedMap(new LinkedHashMap<String, PlatformInfo>());
		this.debugtext	= "";
		this.peerid	= null;
		this.peerstate	= -1;
		new Thread(this).start();
	}
	
	//-------- methods --------
	
	/**
	 *  Get the peer url.
	 */
	public String	getUrl()
	{
		return url;
	}

	/**
	 *  Set the sent state of the peer.
	 */
	public void	setSent(boolean sent)
	{
		this.sent	= sent;
		addDebugText(2, "set sent to "+sent);
	}
	
	/**
	 *  Set the connection state of the peer.
	 */
	public void	setConnected(boolean connected)
	{
		if(this.connected!=connected)
		{
			addDebugText(1, "Peer "+(connected?"online":"offline"));
		}
		this.connected	= connected;
		if(!connected)
		{
			// Resend current awareness infos on next reconnect.
			sent	= false;
		}
	}
	
	/**
	 *  Check if the peer is connected.
	 */
	public boolean	isConnected()
	{
		return connected;
	}

	/**
	 *  Check if awareness infos have bneen sent.
	 */
	public boolean	isSent()
	{
		return sent;
	}

	/**
	 *  Check if the peer is an initial peer.
	 *  Initial peers are not removed from the list, even when currently offline.
	 */
	public boolean isInitial()
	{
		return initial;
	}
	
	/**
	 *  Update the platform info.
	 *  If the platform is offline, the info is removed.
	 */
	public void	updatePlatformInfo(PlatformInfo info)
	{
		if(info.getDisconnectDate()!=null || info.getAwarenessInfo()!=null
			&& AwarenessInfo.STATE_OFFLINE.equals(info.getAwarenessInfo().getState()))
		{
			addDebugText(3, "Remove platform "+info.getId());
			infos.remove(info.getId());
		}
		else
		{
			addDebugText(3, "Add/update platform "+info.getId());
			infos.put(info.getId(), info);
		}
	}
	
	/**
	 *  Get the platform infos received from the peer.
	 */
	public PlatformInfo[]	getPlatformInfos()
	{
		return infos.values().toArray(new PlatformInfo[0]);
	}
	
	/**
	 *  Clear the list of platform infos.
	 */
	public void	clearPlatformInfos()
	{
		addDebugText(2, "Clear platforms");
		infos.clear();
	}
	
	/**
	 *  Get the host name of the peer server.
	 */
	public String	getHost()
	{
		String	ret	= null;
		try
		{
			ret	= new URL(RelayConnectionManager.httpAddress(url)).getHost();
		}
		catch(MalformedURLException e)
		{
			RelayHandler.getLogger().warning(""+e);
		}
		return ret;
	}

	/**
	 *  Get the location (i.e. city, region, country).
	 *  Dynamically resolved by GeoIP, if available.
	 */
	public String	getLocation()
	{
		return GeoIPService.getGeoIPService().getLocation(getHost());
	}
	
	/**
	 *  Get the country code (e.g. de, us, ...).
	 *  Dynamically resolved by GeoIP, if available.
	 */
	public String	getCountryCode()
	{
		return GeoIPService.getGeoIPService().getCountryCode(getHost());
	}
	
	/**
	 *  Get the location as latitude,longitude.
	 */
	public String	getPosition()
	{
		return GeoIPService.getGeoIPService().getPosition(getHost());
	}
	
	/**
	 *  Get the debug text.
	 */
	public String	getDebugText()
	{
		return debugtext;
	}
	
	/**
	 *  Add a debug message.
	 */
	public synchronized void	addDebugText(int level, String msg)
	{
		if(handler.getSettings().getDebugLevel()>=level)
		{
			debugtext += new Date().toString()+": "+msg+"&#xD;";
		}
	}

	/**
	 *  Check if a platform is connected to this peer.
	 */
	public boolean checkPlatform(String id)
	{
		return infos.containsKey(id);
	}
	
	/**
	 *  Execute the handler.
	 */
	public void run()
	{
		while(!shutdown)
		{
			// Perform DB synchronization, if required.
			if(handler.getSettings().isDBSync() && peerstate!=-1 && handler.getStatisticsDB()!=null)
			{
				int	localstate	= handler.getStatisticsDB().getLatestEntry(peerid);
				if(localstate<peerstate)
				{
					try
					{
						addDebugText(2, "DB synchronization with: "+getUrl()+", local="+localstate+", remote="+peerstate);
						RelayHandler.getLogger().info("Start DB synchronization with: "+getUrl()+", local="+localstate+", remote="+peerstate);
						// Fetch update from remote peer
						byte[]	infos	= handler.getConnectionManager().getDBEntries(getUrl(), peerid, localstate+1, 1000);	// Hack!!! Update (only) 1000 entries per 30 seconds!?
						RemoteMarshalingConfig rmc = new RemoteMarshalingConfig();
//						PlatformInfo[]	pinfos	= (PlatformInfo[])MapSendTask.decodeMessage(infos, null, rmc.getAllSerializers(), rmc.getAllCodecs(), getClass().getClassLoader(), null);	// Hack!!! Use codec factory from relay handler?
						PlatformInfo[]	pinfos	= (PlatformInfo[]) SBinarySerializer.readObjectFromStream(new ByteArrayInputStream(infos), null, null, getClass().getClassLoader(), null, null);
						for(PlatformInfo info: pinfos)
						{
							handler.getStatisticsDB().save(info);
						}
						RelayHandler.getLogger().info("Finished DB synchronization with: "+getUrl()+", local="+localstate+", remote="+peerstate);
					}
					catch(Exception e)
					{
						StringWriter	sw	= new StringWriter();
						e.printStackTrace(new PrintWriter(sw));
						addDebugText(2, "Exception fetching DB update: "+sw);
						RelayHandler.getLogger().warning("Exception fetching DB update: "+sw);
					}
				}
				peerstate	= -1;
			}
			
			// Else ping remote peer
			else
			{
				boolean	connected	= isConnected();
				try
				{
					// Try to connect and add new peers, if any.
					addDebugText(3, "Pinging peer");
					int	dbstate	= handler.getStatisticsDB()!=null ? handler.getStatisticsDB().getLatestEntry(handler.getSettings().getId()) : -1;
					String	servers	= handler.getConnectionManager().getPeerServers(getUrl(), handler.getSettings().getUrl(), handler.getSettings().getId(), dbstate, !isConnected());
					setConnected(true);
					handler.getPeerList().addPeers(servers, false);
				}
				catch(Exception e)
				{
					StringWriter	sw	= new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					addDebugText(connected ? 2 : 3, "Exception pinging peer: "+sw);
					setConnected(false);
				}
				
				if(isConnected())
				{
					if(connected!=isConnected())
					{
						peerOnline();
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
				else if(isInitial())
				{
					if(connected!=isConnected())
					{
						peerOffline();
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
					handler.getPeerList().removePeer(this);
					peerOffline();
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
	
	//-------- helper methods --------
	
	/**
	 *  Called when the peer becomes online.
	 */
	protected void	peerOnline()
	{
		RelayHandler.getLogger().info("Peer added: "+getUrl());
		if(!isSent())
		{
			setSent(true);
			handler.sendPlatformInfos(this, handler.getCurrentPlatforms());
		}		
	}
	
	/**
	 *  Called when a peer becomes offline.
	 */
	protected void	peerOffline()
	{
		RelayHandler.getLogger().info("Peer removed: "+getUrl());

		// Send offline infos for previous platforms.
		PlatformInfo[]	infos	= getPlatformInfos();
		clearPlatformInfos();
		setSent(false);
		
		for(PlatformInfo info: infos)
		{
			// Test if platform is already connected to another peer.
			if(info.getAwarenessInfo()!=null && !handler.getPeerList().checkPlatform(info.getId()))
			{
				AwarenessInfo	awainfo	= info.getAwarenessInfo();
				awainfo.setState(AwarenessInfo.STATE_OFFLINE);
				handler.sendAwarenessInfos(awainfo, false, false);
			}
		}
	}
}