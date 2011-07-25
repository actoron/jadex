package jadex.micro.examples.helloworld;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;

/**
 *  The micro version of the hello world agent.
 */
@Agent
@Description("This agent prints out a hello message.")
@Arguments(@Argument(name="welcome text", description= "This parameter is the text printed by the agent.", 
	clazz=String.class, defaultvalue="\"Hello world, this is a Jadex micro agent.\""))
public class PojoHelloWorldAgent
{
	//-------- attributes --------
	
	/** The micro agent class. */
	@Agent
	protected MicroAgent agent;
	
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	@AgentBody
	public void executeBody()
	{
		System.out.println(agent.getArgument("welcome text"));
		agent.waitFor(2000, new IComponentStep()
		{			
			public Object execute(IInternalAccess ia)
			{
				System.out.println("Good bye world.");
				agent.killAgent();
				return null;
			}
		});
	}
}
