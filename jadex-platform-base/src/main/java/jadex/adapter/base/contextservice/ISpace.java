package jadex.adapter.base.contextservice;

import jadex.bridge.IAgentIdentifier;

/**
 *  Interface for spaces.
 */
public interface ISpace
{
	/**
	 *  Get the space name.
	 *  @return The name.
	 */
	public String getName();
	
	/**
	 *  Called from application context, when an agent was added.
	 *  Also called once for all agents in the context, when a space
	 *  is newly added to the context.
	 *  @param aid	The id of the added agent.
	 */
	public void	agentAdded(IAgentIdentifier aid);

	/**
	 *  Called from application context, when an agent was removed.
	 *  @param aid	The id of the removed agent.
	 */
	public void	agentRemoved(IAgentIdentifier aid);
}
