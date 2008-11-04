package jadex.bridge;

import java.util.Map;

/**
 *  Interface for a Jadex agent factory
 *  (a factory typically belongs to a specific kernel).
 */
public interface IJadexAgentFactory
{
	/**
	 *  Create a Jadex agent.
	 *  @param adapter	The platform adapter for the agent. 
	 *  @param model	The agent model file (i.e. the name of the XML file).
	 *  @param config	The name of the configuration (or null for default configuration) 
	 *  @param arguments	The arguments for the agent as name/value pairs.
	 *  @return	An instance of a jadex agent.
	 */
	public IJadexAgent createJadexAgent(IAgentAdapter adapter, String model, String config, Map arguments);
	
	/**
	 *  Load a Jadex model.
	 *  @param model The model.
	 *  @return The loaded model.
	 */
	public IJadexModel loadModel(String model);

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
}
