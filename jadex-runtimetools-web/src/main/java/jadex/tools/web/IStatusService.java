package jadex.tools.web;

import java.util.Collection;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.transport.PlatformData;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

@Service(system=true)
public interface IStatusService
{
	/**
	 *  Get the established connections.
	 *  @return A list of connections.
	 */
	// No intermediate for easier REST?
	public IFuture<Collection<PlatformData>>	getConnectedPlatforms();

	/**
	 *  Get events about established connections.
	 *  @return Events for connections.
	 */
	public ISubscriptionIntermediateFuture<PlatformData>	subscribeToConnections();
}
