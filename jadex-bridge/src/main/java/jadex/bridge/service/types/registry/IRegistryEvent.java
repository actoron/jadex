package jadex.bridge.service.types.registry;

import java.util.Map;
import java.util.Set;

import jadex.bridge.ClassInfo;
import jadex.bridge.service.IService;
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
	public Map<ClassInfo, Set<IService>> getAddedServices();
//	public Set<IServiceIdentifier> getAddedServices();

	/**
	 *  Get the removedservices.
	 *  @return The removedservices
	 */
	public Map<ClassInfo, Set<IService>> getRemovedServices();
//	public Set<IServiceIdentifier> getAddedServices();
	
	/**
	 *  Get the size of the event in terms of the number of subevents.
	 *  @return The sumber of contained changes.
	 */
	public int size();
}
