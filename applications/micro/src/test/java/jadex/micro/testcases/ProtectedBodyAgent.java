package jadex.micro.testcases;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.OnInit;
import jadex.micro.annotation.OnStart;

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
	//@AgentCreated
	@OnInit
	public void agentCreated()
	{
//		System.out.println("invoked created");
	}
	
	/**
	 *  Perform the tests
	 */
	//@AgentBody
	@OnStart
	protected void executeBody()
	{
		System.out.println("invoked body");
	}
}
