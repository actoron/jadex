package jadex.platform.service.registry;

import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.registry.ARegistryEvent;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Info struct for some known peer.
 */
public class PeerInfo
{
	/** The component identifier of the platform. */
	protected IComponentIdentifier platformid;
	
	/** The indirectly managed clients (sent through a level 1 superpeer) . */
	protected Set<IComponentIdentifier> indirectclients;

	/** The timestamp of the last received message. */
	protected long timestamp;
	
	/** The subscription. */
	protected ISubscriptionIntermediateFuture<ARegistryEvent> subscription;

	/**
	 * Create a new RegistrySynchronizationService.
	 */
	public PeerInfo(IComponentIdentifier platformid)
	{
		this(platformid, null);
	}
	
	/**
	 * Create a new RegistrySynchronizationService.
	 */
	public PeerInfo(IComponentIdentifier platformid, ISubscriptionIntermediateFuture<ARegistryEvent> subscription)
	{
		this.platformid = platformid;
		this.subscription = subscription;
	}

	/**
	 *  Get the timestamp.
	 *  @return the timestamp
	 */
	public long getTimestamp()
	{
		return timestamp;
	}

	/**
	 *  Set the timestamp.
	 *  @param timestamp The timestamp to set
	 */
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

	/**
	 *  Get the platformId.
	 *  @return The platformId
	 */
	public IComponentIdentifier getPlatformId()
	{
		return platformid;
	}

	/**
	 *  Set the platformId.
	 *  @param platformId The platformId to set
	 */
	public void setPlatformId(IComponentIdentifier platformId)
	{
		this.platformid = platformId;
	}

	/**
	 *  Get the indirect clients.
	 *  @return The indirect clients
	 */
	public Set<IComponentIdentifier> getIndirectClients()
	{
		return indirectclients;
	}

	/**
	 *  Get the indirect clients.
	 *  @param indirectclients The indirect clients to set.
	 */
	public void setIndirectClients(Set<IComponentIdentifier> indirectclients)
	{
		this.indirectclients = indirectclients;
	}
	
	/**
	 *  Get the subscription.
	 *  @return the subscription
	 */
	public ISubscriptionIntermediateFuture<ARegistryEvent> getSubscription()
	{
		return subscription;
	}

	/**
	 *  Set the subscription.
	 *  @param subscription The subscription to set
	 */
	public void setSubscription(ISubscriptionIntermediateFuture<ARegistryEvent> subscription)
	{
		this.subscription = subscription;
	}
}