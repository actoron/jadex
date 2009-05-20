package jadex.adapter.base.envsupport.environment;

import jadex.bridge.IAgentIdentifier;
import jadex.bridge.ISpace;
import jadex.commons.IPropertyObject;

/**
 *  Interface for percept generators.
 *  Percept generators listen of the environment for interesting
 *  events and process them to percepts for agents. A percept
 *  is meant as a piece of information that is of interest for
 *  an agent.
 */
public interface IPerceptGenerator extends IEnvironmentListener, IPropertyObject
{
	/**
	 *  Called when an agent was added to the space.
	 *  @param agent The agent identifier.
	 *  @param space The space.
	 */
	public void agentAdded(IAgentIdentifier agent, ISpace space);
	
	/**
	 *  Called when an agent was remove from the space.
	 *  @param agent The agent identifier.
	 *  @param space The space.
	 */
	public void agentRemoved(IAgentIdentifier agent, ISpace space);
}
