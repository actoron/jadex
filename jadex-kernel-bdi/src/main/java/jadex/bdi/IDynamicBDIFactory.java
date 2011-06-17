package jadex.bdi;

import jadex.bdi.model.editable.IMECapability;
import jadex.bridge.modelinfo.IModelInfo;

/**
 *  Extended component factory allowing for dynamic model creation.
 */
public interface IDynamicBDIFactory
{
	/**
	 *  Create a new agent model, which can be manually edited before
	 *  starting.
	 *  @param name	A type name for the agent model.
	 *  @param pkg	Optional package for the model.
	 *  @param imports	Optional imports for the model.
	 */
	public IMECapability	createAgentModel(String name, String pkg, String[] imports);

	/**
	 *  Register a manually edited agent model in the factory.
	 *  @param model	The edited agent model.
	 *  @param filename	The filename for accessing the model.
	 *  @return	The startable agent model.
	 */
	public IModelInfo	registerAgentModel(IMECapability model, String filename);
}
