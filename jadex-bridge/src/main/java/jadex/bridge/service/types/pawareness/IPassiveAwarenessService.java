package jadex.bridge.service.types.pawareness;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

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
	 *  Immediately return known platforms and continuously publish newly found platforms.
	 *  Does no active searching.
	 */
	public ISubscriptionIntermediateFuture<IComponentIdentifier>	subscribeToNewPlatforms();
}
