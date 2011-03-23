package jadex.bridge;

import jadex.bridge.service.IService;
import jadex.commons.IPropertiesProvider;
import jadex.commons.future.IFuture;

/**
 *  This service allows storing and retrieving settings
 *  for specific components or services.
 */
public interface ISettingsService extends IService
{
	/**
	 *  Register a property provider.
	 *  Settings of registered property providers will be automatically saved
	 *  and restored, when properties are loaded.
	 *  @param id 	A unique id to identify the properties (e.g. component or service name).
	 *  @param provider 	The properties provider.
	 */
	public IFuture	registerPropertiesProvider(String id, IPropertiesProvider provider);
	
	/**
	 *  Deregister a property provider.
	 *  Settings of a deregistered property provider will be saved
	 *  before the property provider is removed.
	 *  @param id 	A unique id to identify the properties (e.g. component or service name).
	 */
	public IFuture	deregisterPropertiesProvider(String id);
}
