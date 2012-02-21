package jadex.micro.testcases;

import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;

/**
 *  Test what happens if an exception is thrown in body.
 */
public class BodyExceptionAgent extends MicroAgent
{
	/**
	 *  The agent body.
	 */
	public IFuture<Void> executeBody()
	{
//		System.out.println("execute ExceptionTest ...");
		throw new RuntimeException("Exception in agent body");
//		System.out.println("... finished");
	}
}
