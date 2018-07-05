package jadex.micro.benchmarks;

import jadex.bridge.IInternalAccess;
import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;


/**
 *  Agent creation benchmark. 
 */
@Arguments({
	@Argument(name="num", defaultvalue="1", clazz=int.class)
})
@Description("Peer agent started from MegaParallelStarterAgent.")
@Agent(synchronous=Boolean3.FALSE)
public class MegaParallelCreationAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The number of the agent. */
	@AgentArgument
	protected int num;
	
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		System.out.println("Created peer: "+num+" "+agent.getIdentifier());		
		return new Future<Void>(); // never kill?!
	}

}
