package jadex.bridge;

/**
 *  Interface for agent identifiers.
 */
public interface IAgentIdentifier
{
	/**
	 *  Get the agent name.
	 *  @return The name of an agent
	 */
	public String getName();
	
	/**
	 *  Get the local agent name.
	 *  @return the local name of an agent
	 */
	public String getLocalName();

	/**
	 *  Get the platform name.
	 *  @return The platform name.
	 */
	public String getPlatformName();
	
	/**
	 *  Get the addresses.
	 *  @return The addresses.
	 */
	public String[] getAddresses();
}
