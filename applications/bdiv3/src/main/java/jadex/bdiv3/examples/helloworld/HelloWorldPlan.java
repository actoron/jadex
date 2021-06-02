package jadex.bdiv3.examples.helloworld;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.OnStart;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Imports;

/**
 *  Hello World with plan driven print out.
 */
@Agent(type=BDIAgentFactory.TYPE, keepalive=Boolean3.FALSE)
@Imports({"java.util.logging.*"})
//@Properties({@NameValue(name="logging.level", value="Level.INFO")})
@Description("Hello world agent that creates a hello plan.")
public class HelloWorldPlan
{
	/** The bdi agent. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  The agent body.
	 */
	//@AgentBody
	@OnStart
	public void body()
	{
		agent.getFeature(IBDIAgentFeature.class).adoptPlan("printHello").get();
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
