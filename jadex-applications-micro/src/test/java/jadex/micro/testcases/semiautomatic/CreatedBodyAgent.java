package jadex.micro.testcases.semiautomatic;

import jadex.bridge.IInternalAccess;
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
	@AgentCreated
	public void agentCreated()
	{
		System.out.println("created");
//		return IFuture.DONE;
	}
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void executeBody()
	{
		System.out.println("body");
	}
}
