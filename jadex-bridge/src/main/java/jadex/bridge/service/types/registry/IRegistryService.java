package jadex.bridge.service.types.registry;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Service for registries.
 */
@Service
public interface IRegistryService
{
	/**
	 *  Subscribe to change events of the registry. 
	 */
	public ISubscriptionIntermediateFuture<IRegistryEvent> subscribeToEvents();
	
	
}
