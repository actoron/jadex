package jadex.adapter.base;

import jadex.bridge.IAgentIdentifier;

/**
 *  A context represents an abstract grouping of agents.
 */
public interface IContext
{
	/**
	 *  Get the name of the context.
	 */
	public String	getName();
	
	/**
	 *  Get the type of the context.
	 */
	public String	getType();
	
	/**
	 *  Get the parent of the context (if any).
	 */
	public IContext	getParent();
	
	/**
	 *  Get the children of the context (if any).
	 */
	public IContext[]	getChildren();
	
	/**
	 *  Add an agent to a context.
	 */
	public void	addAgent(IAgentIdentifier agent);
	
	/**
	 *  Remove an agent from a context.
	 */
	public void	removeAgent(IAgentIdentifier agent);

	/**
	 *  Test if an agent is contained in a context.
	 */
	public boolean	containsAgent(IAgentIdentifier agent);

	/**
	 *  Get all agents directly contained in the context (if any).
	 */
	public IAgentIdentifier[]	getAgents();
}
