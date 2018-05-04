package jadex.bridge.service.types.transport;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.transformation.annotations.Include;

/**
 *  Transfer object for information about a connected platform.
 *  1: platform id,
 *  2: protocol name,
 *  3: ready flag (false=connecting, true=connected, null=disconnected).
 */
public class PlatformData
{
	//-------- attributes --------
	
	/** The platform. */
	@Include
	protected IComponentIdentifier	platform;
	
	/** The protocol name (i.e. url prefix). */
	@Include
	protected String	protocol; 
	
	/** The connection state (false=connecting, true=connected, null=disconnected-> i.e. offline event). */
	@Include
	protected Boolean	connected; 
	
	//-------- constructors --------
	
	/**
	 *  Create a new platform data.
	 */
	public PlatformData()
	{
		// bean constructor
	}
	
	/**
	 *  Create a new platform data.
	 */
	public PlatformData(IComponentIdentifier platform, String protocol, Boolean connected)
	{
		this.platform	= platform;
		this.protocol	= protocol;
		this.connected	= connected;
	}
}
