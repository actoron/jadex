package jadex.bdi.runtime.impl;

import jadex.rules.state.IOAVState;

/**
 *  Flyweight for an agent.
 */
public class AgentFlyweight extends CapabilityFlyweight
{
	//-------- constructors --------
	
	/**
	 *  Create a new agent flyweight.
	 *  @param state	The state.
	 *  @param agent	The agent handle.
	 */
	public AgentFlyweight(IOAVState state, Object agent)
	{
		super(state, agent);
	}
}
