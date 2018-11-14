package jadex.micro.testcases;

import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Description;

/**
 *  Agent that produces an exception during init.
 */
@Description("Agent that produces an exception during init.")
@Agent
public class PojoBrokenInitAgent
{
	/**
	 *  Init the agent.
	 */
	@AgentCreated
	public IFuture<Void> agentCreated()
	{
		throw new RuntimeException("Exception in init.");
	}
}
