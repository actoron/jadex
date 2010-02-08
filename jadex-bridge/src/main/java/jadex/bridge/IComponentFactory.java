package jadex.bridge;

import jadex.service.IService;

import java.util.Map;

import javax.swing.Icon;

/**
 *  A factory is responsible for one or more component types
 *  and is capable of loading component models from files
 *  as well as instantiating components.
 */
public interface IComponentFactory
{
	//-------- methods --------
	
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @return The loaded model.
	 */
	public ILoadableComponentModel loadModel(String model);

	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model (e.g. file name).
	 *  @return True, if model can be loaded.
	 */
	public boolean isLoadable(String model);
	
	/**
	 *  Test if a model is startable (e.g. an agent).
	 *  @param model The model (e.g. file name).
	 *  @return True, if startable (and loadable).
	 */
	public boolean isStartable(String model);

	/**
	 *  Get the names of component types supported by this factory.
	 */
	public String[] getComponentTypes();

	/**
	 *  Get a default icon for a component type.
	 */
	public Icon getComponentTypeIcon(String type);

	/**
	 *  Get the component type of a model.
	 *  @param model The model (e.g. file name).
	 */
	public String getComponentType(String model);
	
	/**
	 * Create a component instance.
	 * @param adapter The component adapter.
	 * @param model The component model.
	 * @param config The name of the configuration (or null for default configuration) 
	 * @param arguments The arguments for the agent as name/value pairs.
	 * @param parent The parent component (if any).
	 * @return An instance of a component.
	 */
	public IComponentInstance createComponentInstance(IComponentAdapter adapter, ILoadableComponentModel model, String config, Map arguments, IExternalAccess parent);

	/**
	 *  Get the properties (name/value pairs).
	 *  Arbitrary properties that can e.g. be used to
	 *  define kernel-specific settings to configure tools.
	 *  @param type	The component type. 
	 *  @return The properties or null, if the component type is not supported by this factory.
	 */
	public Map	getProperties(String type);
}
