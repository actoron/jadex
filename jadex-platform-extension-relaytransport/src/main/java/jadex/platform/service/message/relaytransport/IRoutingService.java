package jadex.platform.service.message.relaytransport;

import java.util.LinkedHashSet;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IIntermediateFuture;

public interface IRoutingService
{
	/**
	 *  Attempts to find a route to a destination.
	 * 
	 *  @param destination The destination.
	 *  @param hops Previous hops.
	 *  @return Route cost when routing via this route (multiple returns with different costs possible).
	 */
	public IIntermediateFuture<Integer> discoverRoute(IComponentIdentifier destination, LinkedHashSet<IComponentIdentifier> hops);
}
