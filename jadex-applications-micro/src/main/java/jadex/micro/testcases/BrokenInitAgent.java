package jadex.micro.testcases;

import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;

/**
 *  Agent that produces an exception during init.
 */
@Description("Agent that produces an exception during init.")
public class BrokenInitAgent extends MicroAgent
{
	/**
	 *  Init the agent.
	 */
	public IFuture<Void> agentCreated()
	{
		throw new RuntimeException("Exception in init.");
	}
}
