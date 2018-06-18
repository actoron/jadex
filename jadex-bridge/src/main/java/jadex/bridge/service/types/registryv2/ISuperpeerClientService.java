package jadex.bridge.service.types.registryv2;

import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.search.ServiceEvent;
import jadex.commons.future.ISubscriptionIntermediateFuture;

public interface ISuperpeerClientService
{
	/**
	 *  Subscribes a superpeer to the client,
	 *  receiving events of services added to or
	 *  removed from the client.
	 *  
	 *  @return Added/removed service events.
	 */
	public ISubscriptionIntermediateFuture<ServiceEvent<IServiceIdentifier>> addSuperpeerSubscription(String networkname);
}
