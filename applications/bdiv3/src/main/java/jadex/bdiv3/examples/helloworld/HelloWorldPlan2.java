package jadex.bdiv3.examples.helloworld;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.IBDIAgent;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;

@Agent(type=BDIAgentFactory.TYPE, keepalive=Boolean3.FALSE)
@Description("Hello world agent that creates a hello plan.")
public abstract class HelloWorldPlan2 implements IBDIAgent
{
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		adoptPlan("printHello").get();
	}
	
	/**
	 *  Plan that prints out goal text and passes.
	 */
	@Plan
	protected void printHello(IPlan plan)
	{
		System.out.println("Hello World.");
		plan.waitFor(1000).get();
		System.out.println("Good bye.");
	}
}
