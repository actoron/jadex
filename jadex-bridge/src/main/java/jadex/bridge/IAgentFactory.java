package jadex.bridge;

import java.util.Map;

import javax.swing.Icon;

/**
 *  Interface for an agent factory
 *  (a factory typically belongs to a specific kernel).
 */
public interface IAgentFactory
{
	/**
	 *  Create a kernel agent.
	 *  @param adapter	The platform adapter for the agent. 
	 *  @param model	The agent model file (i.e. the name of the XML file).
	 *  @param config	The name of the configuration (or null for default configuration) 
	 *  @param arguments	The arguments for the agent as name/value pairs.
	 *  @return	An instance of a kernel agent.
	 */
	public IKernelAgent createKernelAgent(IAgentAdapter adapter, String model, String config, Map arguments);
	
	/**
	 *  Load an agent model.
	 *  @param model The model.
	 *  @return The loaded model.
	 */
	public IAgentModel loadModel(String model);

	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model.
	 *  @return True, if model can be loaded.
	 */
	public boolean isLoadable(String model);
	
	/**
	 *  Test if a model is startable (e.g. an agent).
	 *  @param model The model.
	 *  @return True, if startable (and loadable).
	 */
	public boolean isStartable(String model);

	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public String[] getFileTypes();

	/**
	 *  Get a default icon for a file type.
	 */
	public Icon getFileTypeIcon(String type);
}
