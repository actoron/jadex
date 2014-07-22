package jadex.bridge.service.types.factory;

import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.IPersistInfo;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;

import java.util.Map;


/**
 *  A factory is responsible for one or more component types
 *  and is capable of loading component models from files
 *  as well as instantiating components.
 *  
 *  The classloader must be part of the loadModel (and other) methods as
 *  it represents the context. If file names are fully qualified no context
 *  is needed but if shortcut notations are used it is unknown where the
 *  file is located.
 */
@Service
public interface IComponentFactory
{
	/**
	 *  Get a default icon for a component type.
	 */
	public @Reference(remote=false) IFuture<byte[]> getComponentTypeIcon(String type);

	//-------- cached --------
	
	/**
	 *  Get the names of component types supported by this factory.
	 */
	public String[] getComponentTypes();

	//-------- excluded --------
		
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public IFuture<IModelInfo> loadModel(String model, String[] imports, IResourceIdentifier rid);

	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if model can be loaded.
	 */
	public IFuture<Boolean> isLoadable(String model, String[] imports, IResourceIdentifier rid);
	
	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public IFuture<Boolean> isStartable(String model, String[] imports, IResourceIdentifier rid);

	/**
	 *  Get the component type of a model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 */
	public IFuture<String> getComponentType(String model, String[] imports, IResourceIdentifier rid);

	/**
	 *  Get the properties (name/value pairs).
	 *  Arbitrary properties that can e.g. be used to
	 *  define kernel-specific settings to configure tools.
	 *  @param type	The component type. 
	 *  @return The properties or null, if the component type is not supported by this factory.
	 */
	@Excluded
	public Map<String, Object>	getProperties(String type);
	
	/**
	 * Create a component instance.
	 * @param desc	The component description.
	 * @param factory The component adapter factory.
	 * @param model The component model.
	 * @param config The name of the configuration (or null for default configuration) 
	 * @param arguments The arguments for the component as name/value pairs.
	 * @param parent The parent component (if any).
	 * @param bindings	Optional bindings to override bindings from model.
	 * @param copy	Global flag for parameter copying.
	 * @param realtime	Global flag for real time timeouts.
	 * @param persist	Global flag for persistence support.
	 * @param resultlistener	Optional listener to be notified when the component finishes.
	 * @param init	Future to be notified when init of the component is completed.
	 * @return An instance of a component and the corresponding adapter.
	 */
	@Excluded
	public @Reference IFuture<Tuple2<IComponentInstance, IComponentAdapter>> createComponentInstance(@Reference IComponentDescription desc, 
		IComponentAdapterFactory factory, IModelInfo model, String config, Map<String, Object> arguments, 
		IExternalAccess parent, @Reference RequiredServiceBinding[] bindings, boolean copy, boolean realtime, boolean persist,
		IPersistInfo persistinfo, 
		IIntermediateResultListener<Tuple2<String, Object>> resultlistener, Future<Void> init);

}
