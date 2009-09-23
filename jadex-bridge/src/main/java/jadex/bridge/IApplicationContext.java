package jadex.bridge;

import jadex.commons.concurrent.IResultListener;

import java.util.Map;

/**
 *  The application context for agents. Each agent is contained
 *  in exactly 0..1 applications. 
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
	 *  Get the agent type for an agent filename.
	 *  @param aid	The agent filename.
	 *  @return The agent type name.
	 */
	public String getAgentType(String filename);
	
	/**
	 *  Get the agent types.
	 *  @return The agent types.
	 */
	public String[] getAgentTypes();
	
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
	
	/**
	 *  Create an agent in the context.
	 *  @param name	The name of the newly created agent.
	 *  @param type	The agent type as defined in the application type.
	 *  @param configuration	The agent configuration.
	 *  @param arguments	Arguments for the new agent.
	 *  @param start	Should the new agent be started?
	 *  
	 *  @param istener	A listener to be notified, when the agent is created (if any).
	 *  @param creator	The agent that wants to create a new agent (if any).	
	 */
	public void createAgent(String name, final String type, String configuration,
		Map arguments, final boolean start, final boolean master, 
		final IResultListener listener, IAgentIdentifier creator);
}
