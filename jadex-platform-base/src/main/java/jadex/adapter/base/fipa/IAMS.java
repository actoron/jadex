package jadex.adapter.base.fipa;

import jadex.bridge.IAgentIdentifier;
import jadex.commons.concurrent.IResultListener;

import java.util.Map;

/**
 *  Interface for the agent management system (AMS). It provides basic platform 
 *  services for managing agent creation, deletion and search.
 */
public interface IAMS
{
	/**
	 *  Create a new agent on the platform.
	 *  The agent will not run before the {@link startAgent(AgentIdentifier)}
	 *  method is called.
	 *  Ensures (in non error case) that the aid of
	 *  the new agent is added to the AMS when call returns.
	 *  @param name The agent name (null for auto creation)
	 *  @param model The model name.
	 *  @param confi The configuration.
	 *  @param args The arguments map (name->value).
	 *  @param listener The result listener (if any).
	 *  @param creator The creator (if any).
	 */
	public void	createAgent(String name, String model, String config, Map args, IResultListener listener, IAgentIdentifier creator);
	
	/**
	 *  Start a previously created agent on the platform.
	 *  @param agent	The id of the previously created agent.
	 */
	public void	startAgent(IAgentIdentifier agent, IResultListener listener);
	
	/**
	 *  Destroy (forcefully terminate) an agent on the platform.
	 *  @param aid	The agent to destroy.
	 */
	public void destroyAgent(IAgentIdentifier aid, IResultListener listener);

	/**
	 *  Suspend the execution of an agent.
	 *  @param aid The agent identifier.
	 */
	public void suspendAgent(IAgentIdentifier aid, IResultListener listener);
	
	/**
	 *  Resume the execution of an agent.
	 *  @param aid The agent identifier.
	 */
	public void resumeAgent(IAgentIdentifier aid, IResultListener listener);
	
	/**
	 *  Search for agents matching the given description.
	 *  @param adesc The agent description to search (null for any agent).
	 *  @param con The search constraints restricting search and/or result size.
	 *  @return An array of matching agent descriptions.
	 */
	public void searchAgents(IAMSAgentDescription adesc, ISearchConstraints con, IResultListener listener);
	
	/**
	 *  Test if an agent is currently living on the platform.
	 *  @param aid The agent identifier.
	 *  @return True, if agent is hosted on platform.
	 */
	public void containsAgent(IAgentIdentifier aid, IResultListener listener);
	
	/**
	 *  Get the agent adapters.
	 *  @return The agent adapters.
	 */
	public void getAgentIdentifiers(IResultListener listener);
	
	/**
	 *  Get the agent description of a single agent.
	 *  @param aid The agent identifier.
	 *  @return The agent description of this agent.
	 */
	public void getAgentDescription(IAgentIdentifier aid, IResultListener listener);
	
	/**
	 *  Get all agent descriptions.
	 *  @return The agent descriptions of this agent.
	 */
	public void getAgentDescriptions(IResultListener listener);
	
	/**
	 *  Get the number of active agents.
	 *  @param listener The result listener.
	 */
	public void getAgentCount(IResultListener listener);
	
	/**
	 *  Get the agent adapter for an agent identifier.
	 *  @param aid The agent identifier.
	 *  @param listener The result listener.
	 */
	public void getAgentAdapter(IAgentIdentifier aid, IResultListener listener);
	
	/**
	 *  Get the external access of an agent.
	 *  @param aid The agent identifier.
	 *  @param listener The result listener.
	 */
	public void getExternalAccess(IAgentIdentifier aid, IResultListener listener);
	
	//-------- create methods for ams objects --------
	
	/**
	 *  Create an agent identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @return The new agent identifier.
	 */
	public IAgentIdentifier createAgentIdentifier(String name, boolean local);
	
	/**
	 *  Create an agent identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 *  @param resolvers The resolvers.
	 *  @return The new agent identifier.
	 */
	public IAgentIdentifier createAgentIdentifier(String name, boolean local, String[] addresses);
	
	/**
	 *  Create a search constraints object.
	 *  @param maxresults The maximum number of results.
	 *  @param maxdepth The maximal search depth.
	 *  @return The search constraints.
	 */
	public ISearchConstraints createSearchConstraints(int maxresults, int maxdepth);

	/**
	 *  Create a ams agent description.
	 *  @param agent The agent.
	 *  @return The ams agent description.
	 */
	public IAMSAgentDescription createAMSAgentDescription(IAgentIdentifier agent);

	/**
	 *  Create a ams agent description.
	 *  @param agent The agent.
	 *  @param state The state.
	 *  @param ownership The ownership.
	 *  @return The ams agent description.
	 */
	public IAMSAgentDescription createAMSAgentDescription(IAgentIdentifier agent, String state, String ownership);

	/**
	 *  Shutdown the platform.
	 *  @param listener The listener.
	 */
	public void shutdownPlatform(IResultListener listener);

	//-------- listener methods --------
	
	/**
     *  Add an ams listener.
     *  The listener is registered for ams changes.
     *  @param listener  The listener to be added.
     */
    public void addAMSListener(IAMSListener listener);
    
    /**
     *  Remove an ams listener.
     *  @param listener  The listener to be removed.
     */
    public void removeAMSListener(IAMSListener listener);
}
