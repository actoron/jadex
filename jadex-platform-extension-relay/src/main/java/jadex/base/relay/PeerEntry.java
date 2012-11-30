package jadex.base.relay;

import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.platform.service.message.transport.httprelaymtp.RelayConnectionManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 *  Object holding information about a peer relay server.
 */
public class PeerEntry
{
	//-------- attributes --------
	
	/** The URL of the peer. */
	protected String	url;
	
	/** Is the peer one of the initial peers? */
	protected boolean	initial;
	
	/** Is this peer connected? */
	protected boolean	connected;
	
	/** The platform infos received from the peer (platform id->info). */
	protected Map<String, PlatformInfo>	infos;
	
	/** Have initial platform infos been sent already? */
	protected boolean	sent;
	
	/** Debug information as multi-line xml text. */
	protected String	debug;
	
	//-------- constructors --------
	
	/**
	 *  Create a new peer entry.
	 */
	public PeerEntry(String url, boolean initial)
	{
		this.url	= url;
		this.initial	= initial;
		this.infos	= Collections.synchronizedMap(new LinkedHashMap<String, PlatformInfo>());
		this.debug	= "";
//		System.out.println("New peer: "+url);
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
		addDebugText("set sent to "+sent);
	}
	
	/**
	 *  Set the connection state of the peer.
	 */
	public void	setConnected(boolean connected)
	{
		if(this.connected!=connected)
		{
			addDebugText("Peer "+(connected?"online":"offline"));
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
			addDebugText("Remove platform "+info.getId());
			infos.remove(info.getId());
		}
		else
		{
			addDebugText("Add/update platform "+info.getId());
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
		addDebugText("Clear platforms");
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
		return debug;
	}
	
	/**
	 *  Add a debug message.
	 */
	public synchronized void	addDebugText(String msg)
	{
		debug += new Date().toString()+": "+msg+"&#xD;";
	}

	/**
	 *  Check if a platform is connected to this peer.
	 */
	public boolean checkPlatform(String id)
	{
		return infos.containsKey(id);
	}
}
