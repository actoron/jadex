package tutorial;

import jadex.micro.MicroAgent;

/**
 *  A simple agent to be used as a basis for own developments.
 */
public class HelloAgent extends MicroAgent
{
	/**
	 *  Called when the agent is started.
	 */
	public void executeBody()
	{
		System.out.println("Hello world!");
		killAgent();
	}
}
