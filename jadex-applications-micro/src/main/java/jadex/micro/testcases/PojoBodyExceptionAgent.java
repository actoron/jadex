package jadex.micro.testcases;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 *  Test what happens if an exception is thrown in body.
 */
@Agent
public class PojoBodyExceptionAgent
{
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void executeBody()
	{
		System.out.println("execute ExceptionTest ...");
		throw new RuntimeException("Exception in agent body");
//		System.out.println("... finished");
	}
}
