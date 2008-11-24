package jadex.microkernel.examples;

import jadex.microkernel.MicroAgent;

/**
 * 
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
