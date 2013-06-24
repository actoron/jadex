package jadex.bdi;

import jadex.bdi.model.editable.IMECapability;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;

import java.io.InputStream;

/**
 *  Extended component factory allowing for dynamic model creation.
 */
public interface IDynamicBDIFactory
{
	// todo: support dynamic capability also
	
	/**
	 *  Create a new agent model, which can be manually edited before
	 *  starting.
	 *  @param name	A type name for the agent model.
	 *  @param pkg	Optional package for the model.
	 *  @param imports	Optional imports for the model.
	 */
	public @Reference IFuture<IMECapability>	createAgentModel(String name, String pkg, String[] imports, IResourceIdentifier rid);

	/**
	 *  Register a manually edited agent model in the factory.
	 *  @param model	The edited agent model.
	 *  @param filename	The filename for accessing the model.
	 *  @return	The startable agent model.
	 */
	public @Reference IFuture<IModelInfo>	registerAgentModel(@Reference IMECapability model, String filename);
	
	/**
	 *  Load a model from an input stream.
	 *  @param name	The simple model name.
	 *  @param input	The stream with the agent xml.
	 *  @param filename	The full name for accessing the model after loading.
	 *  @param rid	The resource identifier for loading referenced classes etc.
	 *  @return	The startable agent model.
	 */
	public @Reference IFuture<IModelInfo>	loadAgentModel(final String name, @Reference final InputStream input, final String filename, final IResourceIdentifier rid);
}
