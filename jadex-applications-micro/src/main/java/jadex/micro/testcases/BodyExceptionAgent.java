package jadex.micro.testcases;

import jadex.micro.MicroAgent;

/**
 *  Test what happens if an exception is thrown in body.
 */
public class BodyExceptionAgent extends MicroAgent
{
	/**
	 *  The agent body.
	 */
	public void executeBody()
	{
		System.out.println("execute ExceptionTest ...");
		throw new RuntimeException("Exception in agent body");
//		System.out.println("... finished");
	}
}
