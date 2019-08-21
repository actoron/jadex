package tutorial;

import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.OnStart;

/**
 *  A simple agent to be used as a basis for own developments.
 */
@Agent
public class HelloAgent
{
	/**
	 *  Called when the agent is started.
	 */
	//@AgentBody
	@OnStart
	public IFuture<Void> executeBody()
	{
		System.out.println("Hello world!");
		return IFuture.DONE;
	}
}
