package tutorial;

import jadex.bridge.service.annotation.OnStart;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;

/**
 *  A simple agent to be used as a basis for own developments.
 */
@Agent
public class HelloAgent
{
	/**
	 *  Called when the agent is started.
	 */
	@OnStart
	public IFuture<Void> executeBody()
	{
		System.out.println("Hello world!");
		return IFuture.DONE;
	}
}
