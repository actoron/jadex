package jadex.bridge;

import java.util.Map;

/**
 *  Interface for an agent factory
 *  (a factory typically belongs to a specific kernel).
 */
public interface IApplicationFactory extends IComponentFactory
{
	/**
	 *  Create a new agent application.
	 *  @param model	The agent model file (i.e. the name of the XML file).
	 *  @param config	The name of the configuration (or null for default configuration) 
	 *  @param arguments	The arguments for the agent as name/value pairs.
	 *  @return	An instance of the application.
	 */
	public IApplicationContext createApplication(String name, String model, String config, Map arguments) throws Exception;
}
