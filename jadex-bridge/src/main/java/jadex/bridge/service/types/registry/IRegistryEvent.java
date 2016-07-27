package jadex.bridge.service.types.registry;

import java.util.Set;

import jadex.bridge.service.IServiceIdentifier;

/**
 *  Interface for registry events.
 */
public interface IRegistryEvent
{
	/**
	 *  Get the addedservices.
	 *  @return The addedservices
	 */
	public Set<IServiceIdentifier> getAddedServices();

	/**
	 *  Get the removedservices.
	 *  @return The removedservices
	 */
	public Set<IServiceIdentifier> getRemovedServices();
}
