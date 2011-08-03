package jadex.micro.testcases;

import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;

/**
 *  Test what happens if an exception is thrown in body.
 */
public class CreatedExceptionAgent extends MicroAgent
{
	/**
	 *  The agent body.
	 */
	public IFuture agentCreated()
	{
		System.out.println("execute ExceptionTest ...");
		throw new RuntimeException("Exception in agent started");
//		System.out.println("... finished");
	}
}
