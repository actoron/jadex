package jadex.bridge.service.types.pawareness;

import java.util.List;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

/**
 *  Locate other platforms without polling.
 */
@Service(system=true)
public interface IPassiveAwarenessService
{
	/**
	 *  Try to find other platforms and finish after timeout.
	 *  Immediately returns known platforms and concurrently issues a new search, waiting for replies until the timeout.
	 */
	public IIntermediateFuture<IComponentIdentifier>	searchPlatforms();
	
	/**
	 *  Gets the address for a platform ID using the awareness mechanism.
	 * 
	 *  @param platformid The platform ID.
	 *  @return The transport addresses or null if not available.
	 */
	public IFuture<List<TransportAddress>> getPlatformAddresses(IComponentIdentifier platformid);
	
//	/**
//	 *  Immediately return known platforms and continuously publish newly found platforms.
//	 *  Does no active searching.
//	 */
//	// currently unused.
//	public ISubscriptionIntermediateFuture<IComponentIdentifier>	subscribeToNewPlatforms();
}
