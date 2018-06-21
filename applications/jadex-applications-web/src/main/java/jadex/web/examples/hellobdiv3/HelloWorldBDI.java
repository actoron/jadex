package jadex.web.examples.hellobdiv3;

import jadex.bdiv3.IBDIAgent;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

@Agent(keepalive=Boolean3.FALSE)
public abstract class HelloWorldBDI implements IBDIAgent, IHelloService
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
		System.out.println("Hello World, BDI agent was born.");
	}
	
	/**
	 *  Say hello method.
	 */
	public IFuture<String> sayHello()
	{
		return new Future<String>(Math.random()>0.5?  "Hello User": "Hallo Anwender");
	}
}