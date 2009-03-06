package jadex.adapter.base.contextservice;

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
	 *  Get the parent of the context (if any).
	 * /
	public IContext	getParentContext();*/
	
	/**
	 *  Add a sub context.
	 * /
	public void	addSubContext(IContext context);*/
	
	/**
	 *  Remove a sub context.
	 * /
	public void	removeSubContext(IContext context);*/

	/**
	 *  Get the sub contexts of the context (if any).
	 * /
	public IContext[]	getSubContexts();*/
	
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
	
	/**
	 *  Add a space to the context.
	 *  @param space The space.
	 */
	public void addSpace(ISpace space);
	
	/**
	 *  Add a space to the context.
	 *  @param name The space name.
	 */
	public void removeSpace(String name);
	
	/**
	 *  Get a space by name.
	 *  @param name The name.
	 *  @return The space.
	 */
	public ISpace getSpace(String name);
	
	/**
	 *  Add an agent property. 
	 *  @param agent The agent.
	 *  @param key The key.
	 *  @param prop The property.
	 */
	public void addProperty(IAgentIdentifier agent, String key, Object prop);
	
	/**
	 *  Get agent property. 
	 *  @param agent The agent.
	 *  @param key The key.
	 *  @return The property. 
	 */
	public Object getProperty(IAgentIdentifier agent, String key);
}
