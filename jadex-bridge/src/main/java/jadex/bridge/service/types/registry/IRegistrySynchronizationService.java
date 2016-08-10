package jadex.bridge.service.types.registry;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Service for registries that allows subscribing
 *  and following the state of the registry.
 */
@Service
public interface IRegistrySynchronizationService
{
	/**
	 *  Subscribe to change events of the registry. 
	 */
	public ISubscriptionIntermediateFuture<IRegistryEvent> subscribeToEvents();
}
