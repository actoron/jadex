package jadex.micro.testcases;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Description;

/**
 *  Agent with protected body method.
 */
@Agent
@Description("Agent with protected body method.")
public class ProtectedBodyAgent
{
	/**
	 *  Init the agent.
	 */
	@AgentCreated
	public void agentCreated()
	{
//		System.out.println("invoked created");
	}
	
	/**
	 *  Perform the tests
	 */
	@AgentBody
	protected void executeBody()
	{
		System.out.println("invoked body");
	}
}
