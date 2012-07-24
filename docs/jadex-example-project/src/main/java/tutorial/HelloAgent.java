package tutorial;

import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 *  A simple agent to be used as a basis for own developments.
 */
@Agent
public class HelloAgent
{
	/**
	 *  Called when the agent is started.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		System.out.println("Hello world!");
		return IFuture.DONE;
	}
}
