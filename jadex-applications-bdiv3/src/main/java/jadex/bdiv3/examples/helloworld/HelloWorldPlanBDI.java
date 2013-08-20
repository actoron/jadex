package jadex.bdiv3.examples.helloworld;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.runtime.IPlan;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Imports;

/**
 *  Hello World with plan driven print out.
 */
@Agent
@Imports({"java.util.logging.*"})
//@Properties({@NameValue(name="logging.level", value="Level.INFO")})
@Description("Hello world agent that creates a hello plan.")
public class HelloWorldPlanBDI
{
	/** The bdi agent. */
	@Agent
	protected BDIAgent agent;
	
	/**
	 *  The agent body.
	 */
	@AgentBody(keepalive=false)
	public void body()
	{
		agent.adoptPlan("printHello").get();
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
