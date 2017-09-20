package jadex.bridge.service.types.registry;

import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Service for registries that allows subscribing
 *  and following the state of the registry.
 */
@Service(system=true)
// Depending on the supersuperpeer setting make the service unrestricted or default accessible
@Security("%{$args.supersuperpeer? jadex.bridge.service.annotation.Security.UNRESTRICTED: jadex.bridge.service.annotation.Security.DEFAULT}")
public interface ISuperpeerRegistrySynchronizationService
{
	/**
	 *  Subscribe to change events of the registry. 
	 *  This is used by super-peers to exchange and replicate the global registry content.
	 */
	public ISubscriptionIntermediateFuture<IRegistryEvent> subscribeToEvents();
	
	/**
	 *  Update the data of a new or already known client platform on the super-peer.
	 *  This is used by clients to let the super-peer know local changes.
	 *  (This is similar to a reverse subscription. The response tells the client
	 *  how long the lease time is and is the client was removed).
	 */
	public IFuture<RegistryUpdateEvent> updateClientData(IRegistryEvent event); 
	
}
