package jadex.micro.testcases.semiautomatic;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.OnInit;
import jadex.bridge.service.annotation.OnStart;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;

/**
 * 
 */
@Agent
public class CreatedBodyAgent 
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Init the service.
	 */
	//@AgentCreated
	@OnInit
	public void agentCreated()
	{
		System.out.println("created");
//		return IFuture.DONE;
	}
	
	/**
	 *  The agent body.
	 */
	//@AgentBody
	@OnStart
	public void executeBody()
	{
		System.out.println("body");
	}
}
