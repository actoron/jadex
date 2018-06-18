package jadex.bridge.service.types.registryv2;

import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.ServiceEvent;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Service that is called by super peers to receive updates of clients.
 *  Uses subscription futures instead of client calling update method on super peer
 *  to determine automatically, when connection is lost (e.g. client down). 
 */
@Service
public interface ISuperpeerClientService
{
	/**
	 *  Subscribes a super peer to the client,
	 *  receiving events of services added to or
	 *  removed from the client.
	 *  
	 *  @param networkname	The relevant network (i.e. only services for this network will be passed to the super peer).
	 *  @param sp	The super peer.
	 *  
	 *  @return Added/removed service events.
	 */
	public ISubscriptionIntermediateFuture<ServiceEvent<IServiceIdentifier>> addSuperpeerSubscription(String networkname, ISuperpeerService sp);
}
