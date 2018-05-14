package jadex.tools.web;

import java.util.Collection;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.ServiceQuery;
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
	
	/**
	 *  Get registered queries of a given (set of) scope(s) or no scope for all queries.
	 *  @return A list of queries.
	 */
	// No intermediate for easier REST?
	// TODO: subscription in registry to get notified about new queries? -> please no polling!
	public IFuture<Collection<ServiceQuery<?>>>	getQueries(String... scope);
}
