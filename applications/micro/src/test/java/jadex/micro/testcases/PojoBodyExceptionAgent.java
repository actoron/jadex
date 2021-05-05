package jadex.micro.testcases;

import jadex.bridge.service.annotation.OnStart;
import jadex.micro.annotation.Agent;

/**
 *  Test what happens if an exception is thrown in body.
 */
@Agent
public class PojoBodyExceptionAgent
{
	/**
	 *  Create a new agent.
	 */
	public PojoBodyExceptionAgent()
	{
//		throw new RuntimeException("Exception in constructor");
	}
	
	/**
	 *  The agent body.
	 */
	//@AgentBody
	@OnStart
	public void executeBody()
	{
//		System.out.println("execute ExceptionTest ...");
		throw new RuntimeException("Exception in agent body");
//		System.out.println("... finished");
	}
}
