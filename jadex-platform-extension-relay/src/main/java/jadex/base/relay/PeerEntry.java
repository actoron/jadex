package jadex.base.relay;

import jadex.bridge.service.types.awareness.AwarenessInfo;

import java.util.Collections;
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
	
	/** The awareness infos received from the peer (platform id->awa info). */
	protected Map<String, AwarenessInfo>	awainfos;
	
	/** Have initial awareness infos been sent already? */
	protected boolean	sent;
	
	//-------- constructors --------
	
	/**
	 *  Create a new peer entry.
	 */
	public PeerEntry(String url, boolean initial)
	{
		this.url	= url;
		this.initial	= initial;
		this.awainfos	= Collections.synchronizedMap(new LinkedHashMap<String, AwarenessInfo>());
//		System.out.println("New peer: "+url);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the peer url.
	 */
	public String	getURL()
	{
		return url;
	}
	
	/**
	 *  Set the sent state of the peer.
	 */
	public void	setSent(boolean sent)
	{
		this.sent	= sent;
	}
	
	/**
	 *  Set the connection state of the peer.
	 */
	public void	setConnected(boolean connected)
	{
		if(this.connected!=connected)
		{
			System.out.println("Peer "+(connected?"online":"offline")+": "+url);
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
	 *  Update the awareness info.
	 *  If the platform is offline, the info is removed.
	 */
	public void	updateAwarenessInfo(AwarenessInfo awainfo)
	{
		if(AwarenessInfo.STATE_OFFLINE.equals(awainfo.getState()))
		{
			awainfos.remove(awainfo.getSender().getName());
		}
		else
		{
			awainfos.put(awainfo.getSender().getName(), awainfo);
		}
	}
	
	/**
	 *  Get the awareness infos received from the peer.
	 */
	public AwarenessInfo[]	getAwarenessInfos()
	{
		return awainfos.values().toArray(new AwarenessInfo[0]);
	}
}
