package jadex.bridge;

/**
 * 
 */
public interface IApplicationContext extends IContext
{
	//-------- constants --------
	
	/** The application type property (required for context creation). */
	public static final String	PROPERTY_APPLICATION_TYPE	= "application-type";
	
	/** The master flag. */
	public static final String PROPERTY_AGENT_MASTER = "master";
	
	//-------- methods --------
	
	/**
	 *  Get the agent type for an agent id.
	 *  @param aid	The agent id.
	 *  @return The agent type name.
	 */
	public String getAgentType(IAgentIdentifier aid);
	
	/**
	 *  Set an agent as master (causes context to be terminated on its deletion).
	 *  @param agent The agent.
	 *  @param master The master.
	 */
	public void setAgentMaster(IAgentIdentifier agent, boolean master);
	
	/**
	 *  Set an agent as master (causes context to be terminated on its deletion).
	 *  @param agent The agent.
	 *  @return True, if agent is master.
	 */
	public boolean isAgentMaster(IAgentIdentifier agent);
	
	/**
	 *  Get the platform.
	 *  @return The platform.
	 */
	public IPlatform getPlatform();
}
