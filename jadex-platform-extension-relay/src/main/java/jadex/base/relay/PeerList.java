package jadex.base.relay;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import jadex.platform.service.message.transport.httprelaymtp.RelayConnectionManager;

/**
 *  The peer list actively manages the list of
 *  connected peer relay servers.
 */
public class PeerList
{
	//-------- attributes --------
	
	/** The relay handler. */
	protected RelayHandler	handler;
	
	/** The peer handlers for polling relay peers (url -> peer handler). */
	protected Map<String, PeerHandler>	peers;
	
	//-------- constructors --------
	
	/**
	 *  Create a new peer list.
	 */
	public PeerList(RelayHandler handler)
	{
		this.handler	= handler;
		this.peers	= Collections.synchronizedMap(new HashMap<String, PeerHandler>());
	}
	
	/**
	 *  Stop any activities for managing the peer list.
	 */
	public void	dispose()
	{
		for(PeerHandler handler: peers.values().toArray(new PeerHandler[0]))
		{
			handler.shutdown();
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Get the known relay urls.
	 *  If no urls are known, the request url is returned.
	 */
	public String	getURLs(String request)
	{
		String	ret;
		
		// Fallback, when no peers specified.
		if(!handler.getSettings().isUrlSpecified())
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
			sret.append(handler.getSettings().getUrl());
			PeerHandler[] apeers = getPeers();
			for(PeerHandler peer: apeers)
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
	public PeerHandler[] getPeers()
	{
		// Fetch array to avoid concurrency problems
		return peers.values().toArray(new PeerHandler[0]);
	}
	
	/**
	 *  Test if the given platform is connected to some peer.
	 */
	public boolean	checkPlatform(String id)
	{
		boolean	found	= false;
		for(PeerHandler peer: peers.values().toArray(new PeerHandler[0]))
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
	 *  Add peers from a servers list.
	 *  @param peerurls	The remote peer urls (comma separated).
	 *  @param initial	Denotes an initial peer as specified in the properties of this relay. Initial peers are not removed when they are offline.
	 */
	public void	addPeers(String peerurls, boolean initial)
	{
		if(peerurls!=null)
		{
			StringTokenizer	stok	= new StringTokenizer(peerurls, ",");
			while(stok.hasMoreTokens())
			{
				addPeer(stok.nextToken().trim(), null, -1, true);
			}
		}
	}
	
	/**
	 *  Add a peer that requested a connection.
	 *  @param peerurl	The remote peer url.
	 */
	public PeerHandler	addPeer(String peerurl)
	{
		return addPeer(peerurl, null, -1);
	}
	
	/**
	 *  Add a peer that requested a connection.
	 *  Called for the continuous pings sent by connected peers.
	 *  @param peerurl	The remote peer url.
	 *  @param peerid	Contains the id of the remote peer.
	 *  @param peerstate	Contains id of the latest history entry of that peer to enable synchronization.
	 */
	public PeerHandler	addPeer(String peerurl, String peerid, int peerstate)
	{
		return addPeer(peerurl, peerid, peerstate, false);
	}
	
	/**
	 *  Remove a peer from the list.
	 */
	public void removePeer(PeerHandler peer)
	{
		peers.remove(peer.getUrl());
	}
	
	/**
	 *  Add a peer found in a servers list or a peer that requested a connection.
	 *  Also called for the continuous pings sent by connected peers.
	 *  @param peerurl	The remote peer url.
	 *  @param peerid	If called from remote peer, contains the id of that peer.
	 *  @param peerstate	If called from remote peer, contains id of the latest history entry of that peer to enable synchronization.
	 *  @param initial	Denotes an initial peer as specified in the properties of this relay. Initial peers are not removed when they are offline.
	 */
	protected PeerHandler	addPeer(String peerurl, String peerid, int peerstate, boolean initial)
	{
		PeerHandler	peer	= null;
		peerurl	= RelayConnectionManager.httpAddress(peerurl);
		
		if(!handler.getSettings().isUrlSpecified())
		{
			throw new RuntimeException("No peer connections allowed, if public URL not set.");
		}
		
		// Add/update peer if not server itself
		else if(!RelayConnectionManager.isSameServer(handler.getSettings().getUrl(), peerurl))
		{
			synchronized(peers)
			{
				peer	= peers.get(peerurl);
				if(peer==null)
				{
					peer	= new PeerHandler(handler, peerurl, initial);
					peers.put(peerurl, peer);
				}
			}
			
			// DB synchronization.
			if(peerid!=null && peerstate!=-1)
			{
				PeerHandler	handler	= peers.get(peerurl);
				if(handler!=null)
				{
					handler.setPeerState(peerid, peerstate);
				}
			}
		}
		return peer;
	}	
}
