package jadex.platform.service.message.relaytransport;

import java.util.LinkedHashSet;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.annotation.Service;
import jadex.commons.Tuple2;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableFuture;

/**
 *  Service used to find routes for relay transports.
 *
 */
@Service(system=true)
@Security(roles=Security.UNRESTRICTED)
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
	
	public ITerminableFuture<Integer> forwardMessage(IMsgHeader header, byte[] body);
	
	public ISubscriptionIntermediateFuture<Tuple2<IMsgHeader, byte[]>> connect();
}
