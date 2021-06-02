package jadex.bridge.service.types.factory;

import java.util.Collection;
import java.util.Map;

import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.Raw;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;


/**
 *  A factory is responsible for one or more component types
 *  and is capable of loading component models from files
 *  as well as providing component features.
 */
@Service(system=true)
public interface IComponentFactory
{
	/**
	 *  Get a default icon for a component type.
	 */
	public @Reference(remote=false) IFuture<byte[]> getComponentTypeIcon(String type);

	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name or resource name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public IFuture<IModelInfo> loadModel(String model, String[] imports, IResourceIdentifier rid);

	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model (e.g. file name or resource name).
	 *  @param The imports (if any).
	 *  @return True, if model can be loaded.
	 */
	public IFuture<Boolean> isLoadable(String model, String[] imports, IResourceIdentifier rid);
	
	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name or resource name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public IFuture<Boolean> isStartable(String model, String[] imports, IResourceIdentifier rid);

	/**
	 *  Get the component type of a model.
	 *  @param model The model (e.g. file name or resource name).
	 *  @param The imports (if any).
	 */
	public IFuture<String> getComponentType(String model, String[] imports, IResourceIdentifier rid);

	//-------- cached --------
	
	/**
	 *  Get the names of component types supported by this factory.
	 */
	@Raw
	public String[] getComponentTypes();

	//-------- excluded --------
	
	/**
	 *  Get the properties (name/value pairs).
	 *  Arbitrary properties that can e.g. be used to
	 *  define kernel-specific settings to configure tools.
	 *  @param type	The component type. 
	 *  @return The properties or null, if the component type is not supported by this factory.
	 */
	@Excluded
	@Raw
	public Map<String, Object>	getProperties(String type);
	
	/**
	 *  Get the component features for a model.
	 *  @param model The component model.
	 *  @return The component features.
	 */
	@Excluded
	public @Reference(remote=false) IFuture<Collection<IComponentFeatureFactory>> getComponentFeatures(IModelInfo model);
}
