package jadex.bridge.service.types.transport;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Provide information about a transport.
 *  Used, e.g. by relay status page.
 */
@Service(system=true)
public interface ITransportInfoService
{
	/**
	 *  Get events about established connections.
	 *  @return Events for connections specified by
	 *  	1: platform id,
	 *  	2: protocol name,
	 *  	3: ready flag (false=connecting, true=connected, null=disconnected).
	 */
	public ISubscriptionIntermediateFuture<PlatformData>	subscribeToConnections();
	
	/**
	 *  Get the established connections.
	 *  @return A list of connections specified by
	 *  	1: platform id,
	 *  	2: protocol name,
	 *  	3: ready flag (false=connecting, true=connected).
	 */
	public IIntermediateFuture<PlatformData>	getConnections();
}
