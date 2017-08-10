package jadex.bridge.service.types.settings;

import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Service;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.future.IFuture;

/**
 *  This service allows storing and retrieving settings
 *  for specific components or services.
 */
@Service(system=true)
public interface ISettingsService
{
	/**
	 *  Register a property provider.
	 *  Settings of registered property providers will be automatically saved
	 *  and restored, when properties are loaded.
	 *  @param id 	A unique id to identify the properties (e.g. component or service name).
	 *  @param provider 	The properties provider.
	 *  @return A future indicating when registration is finished.
	 */
	public IFuture<Void>	registerPropertiesProvider(String id, @Reference IPropertiesProvider provider);
	
	/**
	 *  Deregister a property provider.
	 *  Settings of a deregistered property provider will be saved
	 *  before the property provider is removed.
	 *  @param id 	A unique id to identify the properties (e.g. component or service name).
	 *  @return A future indicating when registration is finished.
	 */
	public IFuture<Void>	deregisterPropertiesProvider(String id);
	
	/**
	 *  Set the properties for a given id.
	 *  Overwrites existing settings (if any).
	 *  @param id 	A unique id to identify the properties (e.g. component or service name).
	 *  @param properties 	The properties to set.
	 *  @return A future indicating when properties have been set.
	 */
	public IFuture<Void>	setProperties(String id, Properties props);
	
	/**
	 *  Get the properties for a given id.
	 *  @param id 	A unique id to identify the properties (e.g. component or service name).
	 *  @return A future containing the properties (if any).
	 */
	public IFuture<Properties>	getProperties(String id);

	// todo: load/saveProperties(String filename)
	
	/**
	 *  Load the default platform properties.
	 *  @return A future indicating when properties have been loaded.
	 */
//	public IFuture<Properties>	loadProperties();
	public IFuture<Void>	loadProperties();
	
	/**
	 *  Save the platform properties to the default location.
	 *  @return A future indicating when properties have been saved.
	 */
	public IFuture<Void>	saveProperties();
	
	/**
	 *  Set the save on exit policy.
	 *  @param saveonexit The saveonexit flag.
	 */
	public IFuture<Void> setSaveOnExit(boolean saveonexit);
}
