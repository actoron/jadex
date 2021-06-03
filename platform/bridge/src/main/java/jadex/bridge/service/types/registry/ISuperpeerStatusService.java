package jadex.bridge.service.types.registry;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.QueryEvent;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Monitoring service for displaying information about a superpeer, e.g. for GUI.
 */
@Service
public interface ISuperpeerStatusService
{
	/**
	 *  Get the clients that are currently registered to super peer.
	 */
	public ISubscriptionIntermediateFuture<IComponentIdentifier>	getRegisteredClients();
	
	/**
	 *  Get registered queries.
	 *  @return A stream of events for added/removed queries.
	 */
	public ISubscriptionIntermediateFuture<QueryEvent>	subscribeToQueries();
}
