package jadex.bridge;

import java.util.Collection;


/**
 *  The agent platform interface.
 */
public interface IPlatform
{	
	/**
	 *  Get the name of the platform
	 *  @return The name of this platform.
	 */
	public String getName();
	
	/**
	 *  Get a platform service.
	 *  @param type The class.
	 *  @return The corresponding platform services.
	 */
	public Collection getServices(Class type);
	
	/**
	 *  Get a platform service.
	 *  @param name The name.
	 *  @return The corresponding platform service.
	 */
	public Object getService(Class type, String name);
	
	/**
	 *  Get a platform service.
	 *  @param type The service interface/type.
	 *  @return The corresponding platform service.
	 */
	public Object getService(Class type);
		
	/**
	 *  Get the agent factory.
	 *  @return The agent factory.
	 */
	// Todo: remove from external platform interface
	public IJadexAgentFactory getAgentFactory();
	
	/**
	 *  Get the configuration.
	 *  @return The configuration.
	 */
//	public Properties getConfiguration();
	
}
