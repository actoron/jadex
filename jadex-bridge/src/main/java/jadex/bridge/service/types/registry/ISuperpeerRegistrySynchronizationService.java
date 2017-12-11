package jadex.bridge.service.types.registry;

import java.util.Collection;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
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
	/** Predefined supersuperpeers. */
	public static final IComponentIdentifier[] DEFAULT_SUPERSUPERPEERS = new IComponentIdentifier[]
	{
		new ComponentIdentifier("ssp1"),
		new ComponentIdentifier("ssp2"),
		new ComponentIdentifier("ssp3"),
	};
	
	/**
	 *  Subscribe to change events of the registry. 
	 *  This is used by super-peers to exchange and replicate the global registry content.
	 *  @return The registry events.
	 */
	public ISubscriptionIntermediateFuture<ARegistryEvent> subscribeToEvents();
	
	/**
	 *  Update the data of a new or already known client platform on the super-peer.
	 *  This is used by clients to let the super-peer know local changes.
	 *  (This is similar to a reverse subscription. The response tells the client
	 *  how long the lease time is and is the client was removed).
	 *  @param event The event.
	 *  @return The update event.
	 */
	public IFuture<ARegistryResponseEvent> updateClientData(ARegistryEvent event); 
	
	/**
	 *  Get the current partner superpeers.
	 *  @return The partner superpeers of the same level.
	 */
	public IFuture<Collection<IComponentIdentifier>> getPartnerSuperpeers();
	
	/**
	 *  Get the current clients.
	 *  @retrun The clients.
	 */
	public IFuture<Collection<IComponentIdentifier>> getClients();
	
	/**
	 *  Get the level (level 0 is the topmost superpeer level).
	 *  @retrun The level.
	 */
	public IFuture<Integer> getLevel();
}
