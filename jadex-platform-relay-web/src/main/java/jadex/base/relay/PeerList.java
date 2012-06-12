package jadex.base.relay;

import java.util.UUID;

/**
 *  The peer list actively manages the list of
 *  connected peer relay servers.
 */
public class PeerList
{
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
	}
}
