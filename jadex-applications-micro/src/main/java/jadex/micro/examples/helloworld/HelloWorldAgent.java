package jadex.micro.examples.helloworld;

import jadex.microkernel.MicroAgent;

/**
 *  The micro version of the hello world agent.
 */
public class HelloWorldAgent extends MicroAgent
{
	/**
	 *  Execute an agent step.
	 */
	public boolean executeAction()
	{
		System.out.println("Hello world, this is a Jadex micro agent");
		return false;
	}
}
