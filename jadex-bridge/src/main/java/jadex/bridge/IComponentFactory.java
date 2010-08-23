package jadex.bridge;

import jadex.commons.Future;
import jadex.commons.service.IService;

import java.util.Map;

import javax.swing.Icon;

/**
 *  A factory is responsible for one or more component types
 *  and is capable of loading component models from files
 *  as well as instantiating components.
 */
public interface IComponentFactory extends IService
{
	//-------- methods --------
	
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public IModelInfo loadModel(String model, String[] imports, ClassLoader classloader);

	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if model can be loaded.
	 */
	public boolean isLoadable(String model, String[] imports, ClassLoader classloader);
	
	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public boolean isStartable(String model, String[] imports, ClassLoader classloader);

	/**
	 *  Get the component type of a model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 */
	public String getComponentType(String model, String[] imports, ClassLoader classloader);

	/**
	 *  Get the names of component types supported by this factory.
	 */
	public String[] getComponentTypes();

	/**
	 *  Get a default icon for a component type.
	 */
	public Icon getComponentTypeIcon(String type);
	
	/**
	 * Create a component instance.
	 * @param factory The component adapter factory.
	 * @param model The component model.
	 * @param config The name of the configuration (or null for default configuration) 
	 * @param arguments The arguments for the component as name/value pairs.
	 * @param parent The parent component (if any).
	 * @return An instance of a component and the corresponding adapter.
	 */
	public Object[] createComponentInstance(IComponentDescription desc, IComponentAdapterFactory factory, IModelInfo model, 
		String config, Map arguments, IExternalAccess parent, Future ret);

	/**
	 *  Get the properties (name/value pairs).
	 *  Arbitrary properties that can e.g. be used to
	 *  define kernel-specific settings to configure tools.
	 *  @param type	The component type. 
	 *  @return The properties or null, if the component type is not supported by this factory.
	 */
	public Map	getProperties(String type);
}
