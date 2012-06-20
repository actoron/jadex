package jadex.base.relay;


/**
 *  Object holding information about a peer.
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
	
	//-------- constructors --------
	
	/**
	 *  Create a new peer entry.
	 */
	public PeerEntry(String url, boolean initial)
	{
		this.url	= url;
		this.initial	= initial;
		System.out.println("New peer: "+url);
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
	 *  Set the connection state of the peer.
	 */
	public void	setConnected(boolean connected)
	{
		if(this.connected!=connected)
		{
			System.out.println("Peer "+(connected?"online":"offline")+": "+url);
		}
		this.connected	= connected;
	}
	
	/**
	 *  Check if the peer is connected.
	 */
	public boolean	isConnected()
	{
		return connected;
	}

	/**
	 *  Check if the peer is an initial peer.
	 *  Initial peers are not removed from the list, even when currently offline.
	 */
	public boolean isInitial()
	{
		return initial;
	}
}
